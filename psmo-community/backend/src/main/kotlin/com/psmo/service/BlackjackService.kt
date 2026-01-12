package com.psmo.service

import com.psmo.model.*
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class BlackjackService(
    private val userService: UserService
) {
    // In-memory storage for active games. 
    // In production, this should be Redis with TTL.
    private val games = ConcurrentHashMap<String, BlackjackGame>()

    fun startGame(user: User, betAmount: Int): BlackjackGame {
        if (user.score < betAmount) {
            throw IllegalArgumentException("Not enough points")
        }
        if (betAmount <= 0) {
            throw IllegalArgumentException("Invalid bet amount")
        }

        // Deduct bet immediately
        userService.adjustScore(user.id, -betAmount)

        val deck = createDeck().toMutableList()
        val playerHand = mutableListOf(deck.removeAt(0), deck.removeAt(0))
        val dealerHand = mutableListOf(deck.removeAt(0), deck.removeAt(0))

        val game = BlackjackGame(
            userId = user.id,
            betAmount = betAmount,
            deck = deck,
            playerHand = playerHand,
            dealerHand = dealerHand,
            status = GameStatus.PLAYER_TURN
        )

        // Check for natural Blackjack immediately
        if (calculateHandValue(playerHand) == 21) {
            // Check if dealer also has BJ
            if (calculateHandValue(dealerHand) == 21) {
                game.status = GameStatus.FINISHED
                game.result = GameResult.PUSH
                game.payout = betAmount // Return bet
                userService.adjustScore(user.id, betAmount)
            } else {
                game.status = GameStatus.FINISHED
                game.result = GameResult.BLACKJACK
                game.payout = (betAmount * 2.5).toInt() // Pays 3:2 (+ original bet) -> 2.5x
                userService.adjustScore(user.id, game.payout)
            }
        }

        games[game.id] = game
        return game
    }

    fun hit(gameId: String, userId: Long): BlackjackGame {
        val game = games[gameId] ?: throw IllegalArgumentException("Game not found")
        if (game.userId != userId) throw IllegalArgumentException("Not your game")
        if (game.status != GameStatus.PLAYER_TURN) throw IllegalStateException("Cannot hit now")

        val card = game.deck.removeAt(0)
        game.playerHand.add(card)

        val value = calculateHandValue(game.playerHand)
        if (value > 21) {
            // Bust
            game.status = GameStatus.FINISHED
            game.result = GameResult.DEALER_WIN
            game.payout = 0
            // No refund
            games.remove(gameId) // Game over, cleanup
            // Just return the finished state (maybe keeping it in map for a moment if we want to query status?)
            // For now, let's keep it in map for a short time or return it and rely on client.
            // Better to keep in map? No, leak. 
            // We return the object.
        }

        return game
    }

    fun stand(gameId: String, userId: Long): BlackjackGame {
        val game = games[gameId] ?: throw IllegalArgumentException("Game not found")
        if (game.userId != userId) throw IllegalArgumentException("Not your game")
        if (game.status != GameStatus.PLAYER_TURN) throw IllegalStateException("Cannot stand now")

        game.status = GameStatus.DEALER_TURN
        
        // Dealer logic: Hit until 17
        while (calculateHandValue(game.dealerHand) < 17) {
            game.dealerHand.add(game.deck.removeAt(0))
        }

        val playerVal = calculateHandValue(game.playerHand)
        val dealerVal = calculateHandValue(game.dealerHand)
        
        game.status = GameStatus.FINISHED

        if (dealerVal > 21) {
            // Dealer Bust -> Player Win
            game.result = GameResult.PLAYER_WIN
            game.payout = game.betAmount * 2
        } else if (playerVal > dealerVal) {
            // Player Higher -> Win
            game.result = GameResult.PLAYER_WIN
            game.payout = game.betAmount * 2
        } else if (playerVal < dealerVal) {
            // Dealer Higher -> Lose
            game.result = GameResult.DEALER_WIN
            game.payout = 0
        } else {
            // Tie -> Push
            game.result = GameResult.PUSH
            game.payout = game.betAmount
        }

        if (game.payout > 0) {
            userService.adjustScore(userId, game.payout)
        }

        games.remove(gameId) // Cleanup
        return game
    }
    
    fun getGame(gameId: String): BlackjackGame? {
        return games[gameId]
    }

    private fun createDeck(): List<Card> {
        val deck = ArrayList<Card>()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deck.add(Card(suit, rank))
            }
        }
        deck.shuffle()
        return deck
    }

    private fun calculateHandValue(hand: List<Card>): Int {
        var value = 0
        var aces = 0

        for (card in hand) {
            val v = card.rank.value
            if (card.rank == Rank.ACE) {
                aces++
            } else {
                value += v
            }
        }
        
        // Add Aces
        for (i in 0 until aces) {
             value += 11
        }
        
        // Adjust if bust
        while (value > 21 && aces > 0) {
            value -= 10
            aces--
        }
        
        return value
    }
}
