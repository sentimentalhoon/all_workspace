package com.psmo.model.dto

data class BlackjackStartRequestDto(
    val betAmount: Int = 0
)

import com.psmo.model.BlackjackGame
import com.psmo.model.Card
import com.psmo.model.GameResult
import com.psmo.model.GameStatus

data class BlackjackGameResponse(
    val id: String,
    val userId: Long,
    val betAmount: Int,
    val playerHand: List<Card>,
    val dealerHand: List<Card>,
    val status: GameStatus,
    val result: GameResult,
    val payout: Int
)

fun BlackjackGame.toResponse(): BlackjackGameResponse {
    return BlackjackGameResponse(
        id = this.id,
        userId = this.userId,
        betAmount = this.betAmount,
        playerHand = this.playerHand,
        dealerHand = this.dealerHand,
        status = this.status,
        result = this.result,
        payout = this.payout
    )
}
