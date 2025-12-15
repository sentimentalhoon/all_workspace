package com.psmo.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object SnailRaces : Table("snail_races") {
    val raceId = varchar("race_id", 80)
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val snailId = integer("snail_id")
    val betAmount = integer("bet_amount")
    val serverSeed = varchar("server_seed", 128)
    val clientSeed = varchar("client_seed", 128).nullable()
    val combinedSeed = varchar("combined_seed", 256)
    val trackLength = integer("track_length")
    val snailsJson = text("snails_json")
    val fairnessHash = varchar("fairness_hash", 128)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val expiresAt = datetime("expires_at")
    val reported = bool("reported").default(false)
    val verified = bool("verified").nullable()
    val payout = integer("payout").nullable()
    val winnerIds = text("winner_ids").nullable()
    val frames = integer("frames").nullable()
    val durationMs = long("duration_ms").nullable()

    override val primaryKey = PrimaryKey(raceId, name = "pk_snail_races")
}

object SnailRaceLogs : LongIdTable("snail_race_logs") {
    val raceId = varchar("race_id", 80).references(SnailRaces.raceId, onDelete = ReferenceOption.CASCADE)
    val clientLog = text("client_log").nullable()
    val serverState = text("server_state").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

data class SnailRaceRecord(
    val raceId: String,
    val userId: Long,
    val snailId: Int,
    val betAmount: Int,
    val serverSeed: String,
    val clientSeed: String?,
    val combinedSeed: String,
    val trackLength: Int,
    val snailsJson: String,
    val fairnessHash: String,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime,
    val reported: Boolean,
    val verified: Boolean?,
    val payout: Int?,
    val winnerIds: String?,
    val frames: Int?,
    val durationMs: Long?
)

fun ResultRow.toSnailRace(): SnailRaceRecord = SnailRaceRecord(
    raceId = this[SnailRaces.raceId],
    userId = this[SnailRaces.userId],
    snailId = this[SnailRaces.snailId],
    betAmount = this[SnailRaces.betAmount],
    serverSeed = this[SnailRaces.serverSeed],
    clientSeed = this[SnailRaces.clientSeed],
    combinedSeed = this[SnailRaces.combinedSeed],
    trackLength = this[SnailRaces.trackLength],
    snailsJson = this[SnailRaces.snailsJson],
    fairnessHash = this[SnailRaces.fairnessHash],
    createdAt = this[SnailRaces.createdAt],
    expiresAt = this[SnailRaces.expiresAt],
    reported = this[SnailRaces.reported],
    verified = this[SnailRaces.verified],
    payout = this[SnailRaces.payout],
    winnerIds = this[SnailRaces.winnerIds],
    frames = this[SnailRaces.frames],
    durationMs = this[SnailRaces.durationMs]
)
