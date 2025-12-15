package com.psmo.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SnailRunnerDto(
    val id: Int,
    val name: String,
    val color: String,
    val baseSpeed: Double
)

@Serializable
data class SnailRaceLimitsDto(
    val minBet: Int,
    val maxBet: Int,
    val step: Int,
    val cooldownSeconds: Int? = null
)

@Serializable
data class SnailRaceStartRequestDto(
    val snailId: Int,
    val betAmount: Int,
    val clientSeed: String? = null
)

@Serializable
data class SnailRaceStartResponseDto(
    val raceId: String,
    val seed: String,
    val snails: List<SnailRunnerDto>,
    val trackLength: Int,
    val betAmount: Int,
    val balance: Int,
    val fairnessHash: String? = null,
    val limits: SnailRaceLimitsDto? = null
)

@Serializable
data class SnailRaceResultPayloadDto(
    val raceId: String,
    val winnerIds: List<Int>,
    val durationMs: Long,
    val frames: Int,
    val seed: String,
    val betAmount: Int,
    val betSnailId: Int,
    val clientLog: ClientLogDto? = null
)

@Serializable
data class ClientLogDto(
    val positions: List<Int>? = null
)

@Serializable
data class SnailRaceResultResponseDto(
    val raceId: String,
    val verified: Boolean,
    val winnerIds: List<Int>,
    val payout: Int,
    val balance: Int,
    val seed: String,
    val fairnessHash: String? = null,
    val serverNote: String? = null
)
