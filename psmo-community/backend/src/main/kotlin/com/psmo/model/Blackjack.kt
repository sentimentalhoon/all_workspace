package com.psmo.model

import java.time.LocalDateTime
import java.util.UUID

enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

enum class Rank(val value: Int) {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
    JACK(10), QUEEN(10), KING(10), ACE(11)
}

data class Card(val suit: Suit, val rank: Rank) {
    override fun toString(): String {
        return "${rank.name}_OF_${suit.name}"
    }
}

enum class GameStatus {
    PLAYER_TURN,    // Player can Hit or Stand
    DEALER_TURN,    // Player stood, dealer is drawing
    FINISHED        // Game over
}

enum class GameResult {
    NONE,           // Still playing
    PLAYER_WIN,     // Player > Dealer or Dealer bust
    DEALER_WIN,     // Dealer > Player or Player bust
    PUSH,           // Tie
    BLACKJACK       // Player has BJ (Pays 3:2)
}

data class BlackjackGame(
    val id: String = UUID.randomUUID().toString(),
    val userId: Long,
    val betAmount: Int,
    var deck: MutableList<Card>,
    var playerHand: MutableList<Card> = mutableListOf(),
    var dealerHand: MutableList<Card> = mutableListOf(),
    var status: GameStatus = GameStatus.PLAYER_TURN,
    var result: GameResult = GameResult.NONE,
    var payout: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
