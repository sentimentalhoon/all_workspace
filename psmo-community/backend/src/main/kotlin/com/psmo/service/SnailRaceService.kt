package com.psmo.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.psmo.database.DatabaseConfig
import com.psmo.model.SnailRaceLogs
import com.psmo.model.SnailRaces
import com.psmo.model.User
import com.psmo.model.dto.ClientLogDto
import com.psmo.model.dto.SnailRaceLimitsDto
import com.psmo.model.dto.SnailRaceResultPayloadDto
import com.psmo.model.dto.SnailRaceResultResponseDto
import com.psmo.model.dto.SnailRaceStartRequestDto
import com.psmo.model.dto.SnailRaceStartResponseDto
import com.psmo.model.dto.SnailRunnerDto
import com.psmo.model.toSnailRace
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

class SnailRaceService(
    private val config: ApplicationConfig,
    private val userService: UserService,
    private val fairnessService: FairnessService = FairnessService()
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val database by lazy { DatabaseConfig.connectToDatabase(config) }
    private val mapper = jacksonObjectMapper()
    private val limits: SnailRaceLimitsDto = loadLimits(config)
    private val trackLength: Int = config.propertyOrNull("games.snail.trackLength")?.getString()?.toIntOrNull() ?: 520
    private val raceTtl: Duration = config.propertyOrNull("games.snail.raceTtlSeconds")?.getString()?.toLongOrNull()
        ?.let { Duration.ofSeconds(it) } ?: Duration.ofMinutes(10)
    private val payoutMultiplier: Double = config.propertyOrNull("games.snail.payoutMultiplier")?.getString()?.toDoubleOrNull()
        ?: 2.0

    private val cooldowns = ConcurrentHashMap<Long, Instant>()
    private val snails: List<SnailRunnerDto> = listOf(
        // baseSpeed를 동일하게 맞춰 기본 승률 편향을 제거하고, 랜덤 요소에만 의존하게 한다.
        SnailRunnerDto(1, "루나", "#6366f1", 1.0),
        SnailRunnerDto(2, "모코", "#10b981", 1.0),
        SnailRunnerDto(3, "보라", "#f59e0b", 1.0)
    )

    fun startRace(user: User, request: SnailRaceStartRequestDto): SnailRaceStartResponseDto {
        validateCooldown(user.id)
        validateBet(user.score, request.betAmount)

        val serverSeed = fairnessService.generateServerSeed()
        val combinedSeed = fairnessService.combineSeeds(serverSeed, request.clientSeed)
        val fairnessHash = fairnessService.hashSeed(serverSeed)
        val raceId = "sr_${Instant.now().epochSecond}_${UUID.randomUUID().toString().take(8)}"

        transaction(database) {
            SnailRaces.insert {
                it[SnailRaces.raceId] = raceId
                it[SnailRaces.userId] = user.id
                it[SnailRaces.snailId] = request.snailId
                it[SnailRaces.betAmount] = request.betAmount
                it[SnailRaces.serverSeed] = serverSeed
                it[SnailRaces.clientSeed] = request.clientSeed
                it[SnailRaces.combinedSeed] = combinedSeed
                it[SnailRaces.trackLength] = this@SnailRaceService.trackLength
                it[SnailRaces.snailsJson] = mapper.writeValueAsString(snails)
                it[SnailRaces.fairnessHash] = fairnessHash
                it[SnailRaces.expiresAt] = LocalDateTime.ofInstant(Instant.now().plus(raceTtl), ZoneOffset.UTC)
            }
        }
        log.info("snail-race start ok raceId={} userId={} snailId={} bet={}", raceId, user.id, request.snailId, request.betAmount)
        cooldowns[user.id] = Instant.now()

        return SnailRaceStartResponseDto(
            raceId = raceId,
            seed = combinedSeed,
            snails = snails,
            trackLength = trackLength,
            betAmount = request.betAmount,
            balance = user.score,
            fairnessHash = fairnessHash,
            limits = limits
        )
    }

    fun completeRace(user: User, raceId: String, payload: SnailRaceResultPayloadDto): SnailRaceResultResponseDto {
        val session = transaction(database) {
            SnailRaces.selectAll()
                .where { SnailRaces.raceId eq raceId }
                .singleOrNull()
                ?.toSnailRace()
        } ?: throw RaceNotFoundException()

        if (session.userId != user.id) throw RaceForbiddenException()
        if (session.reported) throw RaceAlreadyReportedException()
        if (session.expiresAt.atZone(ZoneOffset.UTC).toInstant().isBefore(Instant.now())) throw RaceExpiredException()
        if (payload.betAmount != session.betAmount) throw BetValidationException("BET_MISMATCH", "요청 베팅 금액이 저장된 값과 다릅니다")
        if (payload.betSnailId != session.snailId) throw BetValidationException("SNAIL_MISMATCH", "선택한 달팽이가 서버 기록과 다릅니다")
        if (payload.seed != session.combinedSeed) throw BetValidationException("SEED_MISMATCH", "시드가 서버 기록과 다릅니다")

        val snailsFromDb: List<SnailRunnerDto> = mapper.readValue(session.snailsJson)
        val serverWinners = simulate(session.combinedSeed, session.trackLength, snailsFromDb)
        val userWon = serverWinners.winnerIds.contains(payload.betSnailId)
        val verified = serverWinners.winnerIds.sorted() == payload.winnerIds.sorted()

        val payout = if (verified && userWon) {
            (payload.betAmount * payoutMultiplier).toInt() - payload.betAmount
        } else {
            -payload.betAmount
        }

        val updatedUser = userService.adjustScore(user.id, payout)
            ?: throw RaceInternalException("사용자 점수 업데이트 실패")

        transaction(database) {
            SnailRaces.update({ SnailRaces.raceId eq raceId }) {
                it[SnailRaces.reported] = true
                it[SnailRaces.verified] = verified
                it[SnailRaces.payout] = payout
                it[SnailRaces.winnerIds] = serverWinners.winnerIds.joinToString(",")
                it[SnailRaces.frames] = serverWinners.frames
                it[SnailRaces.durationMs] = payload.durationMs
            }

            SnailRaceLogs.insert {
                it[SnailRaceLogs.raceId] = raceId
                it[SnailRaceLogs.clientLog] = payload.clientLog?.let { log -> mapper.writeValueAsString(log) }
                it[SnailRaceLogs.serverState] = mapper.writeValueAsString(
                    mapOf(
                        "winnerIds" to serverWinners.winnerIds,
                        "frames" to serverWinners.frames
                    )
                )
            }
        }

        log.info(
            "snail-race complete raceId={} userId={} verified={} payout={} winners={}",
            raceId,
            user.id,
            verified,
            payout,
            serverWinners.winnerIds
        )

        return SnailRaceResultResponseDto(
            raceId = raceId,
            verified = verified,
            winnerIds = serverWinners.winnerIds,
            payout = payout,
            balance = updatedUser.score,
            seed = session.combinedSeed,
            fairnessHash = session.fairnessHash,
            serverNote = if (verified) "replayed and matched" else "client winners differ"
        )
    }

    private fun validateBet(balance: Int, bet: Int) {
        if (bet < limits.minBet) throw BetValidationException("BET_TOO_SMALL", "최소 베팅은 ${limits.minBet}")
        if (bet > limits.maxBet) throw BetValidationException("BET_TOO_LARGE", "최대 베팅은 ${limits.maxBet}")
        if (bet % limits.step != 0) throw BetValidationException("BET_STEP", "${limits.step} 단위로만 베팅 가능합니다")
        if (bet > balance) throw BetValidationException("INSUFFICIENT_BALANCE", "보유 포인트 부족")
    }

    private fun validateCooldown(userId: Long) {
        val last = cooldowns[userId] ?: return
        val now = Instant.now()
        val elapsed = Duration.between(last, now)
        val cooldown = limits.cooldownSeconds?.toLong() ?: 0L
        if (cooldown <= 0) return
        val remaining = cooldown - elapsed.seconds
        if (remaining > 0) throw CooldownException(remaining)
    }

    private fun simulate(seed: String, trackLength: Int, snails: List<SnailRunnerDto>): SimulationResult {
        val rng = fairnessService.rng(seed)
        val state = snails.map { SnailState(it.id, it.baseSpeed, 0.0) }.toMutableList()
        var frames = 0
        while (true) {
            frames += 1
            for (snail in state) {
                // 약간 더 큰 변동성을 주어 승률 편향을 줄이고 재미를 높인다.
                val burst = rng() * 0.9      // 가속 상한 ↑
                val drift = rng() * 0.35     // 감속 상한 ↑
                val delta = snail.baseSpeed + burst - drift * 0.45
                snail.position = max(snail.position + delta, 0.0)
            }
            val reached = state.any { it.position >= trackLength }
            if (reached) break
            if (frames > 10_000) break // safety
        }
        val maxPos = state.maxOf { it.position }
        val winners = state.filter { it.position >= maxPos - 1 }.map { it.id }
        return SimulationResult(winnerIds = winners, frames = frames)
    }

    private data class SnailState(
        val id: Int,
        val baseSpeed: Double,
        var position: Double
    )

    private data class SimulationResult(
        val winnerIds: List<Int>,
        val frames: Int
    )

    companion object {
        private fun loadLimits(config: ApplicationConfig): SnailRaceLimitsDto {
            val minBet = config.propertyOrNull("games.snail.minBet")?.getString()?.toIntOrNull() ?: 100
            val maxBet = config.propertyOrNull("games.snail.maxBet")?.getString()?.toIntOrNull() ?: 5000
            val step = config.propertyOrNull("games.snail.betStep")?.getString()?.toIntOrNull() ?: 50
            val cooldown = config.propertyOrNull("games.snail.cooldownSeconds")?.getString()?.toIntOrNull()
            return SnailRaceLimitsDto(minBet = minBet, maxBet = maxBet, step = step, cooldownSeconds = cooldown)
        }
    }
}

sealed class SnailRaceException(message: String) : RuntimeException(message)
class BetValidationException(val code: String, override val message: String) : SnailRaceException(message)
class CooldownException(val remainingSeconds: Long) : SnailRaceException("쿨다운")
class RaceNotFoundException : SnailRaceException("Race not found")
class RaceForbiddenException : SnailRaceException("Race belongs to another user")
class RaceAlreadyReportedException : SnailRaceException("Race already reported")
class RaceExpiredException : SnailRaceException("Race expired")
class RaceInternalException(message: String) : SnailRaceException(message)
