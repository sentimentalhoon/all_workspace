<template>
  <div class="blackjack-table">
    <!-- Header / Stats -->
    <div class="table-header">
      <div class="balance-chip">
        <span class="label">Balance</span>
        <span class="value">{{ balance.toLocaleString() }}</span>
      </div>
      <div class="bet-chip">
        <span class="label">Bet</span>
        <span class="value">{{ betAmount.toLocaleString() }}</span>
      </div>
    </div>

    <!-- Dealer Area -->
    <div class="area dealer-area">
      <div class="hand-score" v-if="game">Dealer: {{ dealerScore }}</div>
      <div class="cards-row">
        <TransitionGroup name="card-deal">
          <div
            v-for="(card, index) in dealerDisplayHand"
            :key="card.key"
            class="card"
            :class="card.suit"
          >
            <div class="rank">{{ card.rankDisplay }}</div>
            <div class="suit">{{ card.suitIcon }}</div>
            <div class="rank bottom">{{ card.rankDisplay }}</div>
          </div>
          <!-- Hole Card Back -->
          <div v-if="game && game.status === 'PLAYER_TURN'" class="card back" key="hole-card">
            <div class="pattern"></div>
          </div>
        </TransitionGroup>
      </div>
    </div>

    <!-- Center Message -->
    <div class="message-overlay" v-if="resultMessage">
      <h1 :class="resultClass">{{ resultMessage }}</h1>
      <p v-if="payout > 0">+{{ payout.toLocaleString() }}</p>
    </div>

    <!-- Player Area -->
    <div class="area player-area">
      <div class="hand-score" v-if="game">You: {{ playerScore }}</div>
      <div class="cards-row">
        <TransitionGroup name="card-deal">
          <div v-for="card in playerDisplayHand" :key="card.key" class="card" :class="card.suit">
            <div class="rank">{{ card.rankDisplay }}</div>
            <div class="suit">{{ card.suitIcon }}</div>
            <div class="rank bottom">{{ card.rankDisplay }}</div>
          </div>
        </TransitionGroup>
      </div>
    </div>

    <!-- Controls -->
    <div class="controls">
      <div v-if="!game || game.status === 'FINISHED'" class="bet-controls">
        <div class="chip-rack">
          <button
            v-for="amount in [100, 500, 1000, 5000]"
            :key="amount"
            class="chip-btn"
            @click="increaseBet(amount)"
          >
            {{ amount }}
          </button>
          <button class="clear-btn" @click="betAmount = 0">Clear</button>
        </div>
        <button class="action-btn deal-btn" @click="startGame" :disabled="betAmount === 0">
          DEAL
        </button>
      </div>

      <div v-else class="game-controls">
        <button class="action-btn hit-btn" @click="hit" :disabled="game.status !== 'PLAYER_TURN'">
          HIT
        </button>
        <button
          class="action-btn stand-btn"
          @click="stand"
          :disabled="game.status !== 'PLAYER_TURN'"
        >
          STAND
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth' // using alias
import { fetchClient } from '@/utils/api' // using alias
import { computed, ref } from 'vue'

const auth = useAuthStore()
const balance = computed(() => auth.user?.score ?? 0)
const betAmount = ref(100)
const game = ref<any>(null)

// Helper to map card data to display
const mapCard = (c: any, index: number) => {
  const suits: any = { HEARTS: '♥', DIAMONDS: '♦', CLUBS: '♣', SPADES: '♠' }
  const ranks: any = {
    TWO: '2',
    THREE: '3',
    FOUR: '4',
    FIVE: '5',
    SIX: '6',
    SEVEN: '7',
    EIGHT: '8',
    NINE: '9',
    TEN: '10',
    JACK: 'J',
    QUEEN: 'Q',
    KING: 'K',
    ACE: 'A',
  }
  return {
    key: `${c.suit}-${c.rank}-${index}`,
    suit: c.suit,
    suitIcon: suits[c.suit],
    rankDisplay: ranks[c.rank],
  }
}

const dealerDisplayHand = computed(() => game.value?.dealerHand.map(mapCard) ?? [])
const playerDisplayHand = computed(() => game.value?.playerHand.map(mapCard) ?? [])

const playerScore = computed(() => {
  if (!game.value) return 0
  return calculateScore(game.value.playerHand)
})

const dealerScore = computed(() => {
  if (!game.value) return 0
  // If hidden, only show first card? (Actually backend sends full hand but frontend should hide?
  // Wait, backend sends full hand in DTO. We should hide it in UI logic if PLAYER_TURN)
  // Actually for 'Casino Noir' realism, we should only see 1 card.
  // Logic: In mapCard loop above, we render dealerHand.
  // If status is PLAYER_TURN, dealerHand usually has 2 cards.
  // We should only display the first one in the v-for, and show a 'back' card.

  // BUT, the 'dealerDisplayHand' computation above maps ALL cards.
  // Let's filter it in the template or computed.
  if (game.value.status === 'PLAYER_TURN') {
    const visible = game.value.dealerHand.slice(0, 1) // Only first
    return calculateScore(visible)
  }
  return calculateScore(game.value.dealerHand)
})

// We need to override dealerDisplayHand logic to hide hole card
// Actually, let's fix the computed property above
const dealerDisplayHandReal = computed(() => {
  if (!game.value) return []
  if (game.value.status === 'PLAYER_TURN') {
    return [mapCard(game.value.dealerHand[0], 0)]
  }
  return game.value.dealerHand.map(mapCard)
})
// Swap the variable in template to `dealerDisplayHandReal`

// Simple Score calc helper (same as backend essentially, for UI)
const calculateScore = (hand: any[]) => {
  let value = 0,
    aces = 0
  hand.forEach((c) => {
    const val =
      c.rank === 'ACE'
        ? 11
        : ['JACK', 'QUEEN', 'KING'].includes(c.rank)
          ? 10
          : ['TEN'].includes(c.rank)
            ? 10
            : parseInt(cardValue(c.rank))
    if (c.rank === 'ACE') aces++
    value += val
  })
  while (value > 21 && aces > 0) {
    value -= 10
    aces--
  }
  return value
}
const cardValue = (rank: string) => {
  const map: any = { TWO: 2, THREE: 3, FOUR: 4, FIVE: 5, SIX: 6, SEVEN: 7, EIGHT: 8, NINE: 9 }
  return map[rank] || 10
}

const resultMessage = computed(() => {
  if (!game.value || game.value.status !== 'FINISHED') return ''
  const r = game.value.result
  if (r === 'PLAYER_WIN') return 'YOU WIN!'
  if (r === 'DEALER_WIN') return 'DEALER WINS'
  if (r === 'PUSH') return 'PUSH'
  if (r === 'BLACKJACK') return 'BLACKJACK!'
  return ''
})

const resultClass = computed(() => {
  if (!game.value) return ''
  const r = game.value.result
  if (r === 'PLAYER_WIN' || r === 'BLACKJACK') return 'text-gold'
  if (r === 'DEALER_WIN') return 'text-red'
  return 'text-white'
})

const payout = computed(() => game.value?.payout ?? 0)

const increaseBet = (amt: number) => {
  if (betAmount.value + amt <= balance.value) betAmount.value += amt
}

const startGame = async () => {
  try {
    const res = await fetchClient('/api/games/blackjack/start', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ betAmount: betAmount.value }),
    })
    const data = await res.json()
    if (data.error) throw new Error(data.error)
    game.value = data
    // Update balance locally
    auth.updateScore(balance.value - betAmount.value)
  } catch (e) {
    alert(e)
  }
}

const hit = async () => {
  if (!game.value) return
  try {
    const res = await fetchClient(`/api/games/blackjack/${game.value.id}/hit`, { method: 'POST' })
    const data = await res.json()
    game.value = data
    if (data.status === 'FINISHED') updateAuthBalance(data)
  } catch (e) {
    alert(e)
  }
}

const stand = async () => {
  if (!game.value) return
  try {
    const res = await fetchClient(`/api/games/blackjack/${game.value.id}/stand`, { method: 'POST' })
    const data = await res.json()
    game.value = data
    if (data.status === 'FINISHED') updateAuthBalance(data)
  } catch (e) {
    alert(e)
  }
}

const updateAuthBalance = (finalGame: any) => {
  if (finalGame.payout > 0) {
    auth.updateScore(auth.user!.score + finalGame.payout)
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@700&family=Roboto+Mono:wght@500&display=swap');

.blackjack-table {
  background: radial-gradient(circle at center, #35654d 0%, #1a3c2e 100%);
  min-height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 2rem;
  color: #fff;
  font-family: 'Roboto Mono', monospace;
  position: relative;
  overflow: hidden;
  box-shadow: inset 0 0 100px rgba(0, 0, 0, 0.8);
}

.table-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  max-width: 800px;
}

.balance-chip,
.bet-chip {
  background: rgba(0, 0, 0, 0.6);
  padding: 10px 20px;
  border-radius: 30px;
  border: 1px solid #ffd700;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.label {
  font-size: 0.8rem;
  color: #aaa;
}
.value {
  font-size: 1.2rem;
  font-weight: bold;
  color: #ffd700;
}

.area {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 200px;
  width: 100%;
}

.hand-score {
  margin-bottom: 10px;
  font-size: 1.2rem;
  background: rgba(0, 0, 0, 0.5);
  padding: 5px 15px;
  border-radius: 15px;
}

.cards-row {
  display: flex;
  gap: -40px; /* Overlap cards */
  justify-content: center;
  height: 180px;
}

.card {
  width: 110px;
  height: 160px;
  background: white;
  border-radius: 10px;
  color: black;
  position: relative;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10px;
  transition: all 0.5s ease;
  margin-right: -50px; /* Overlap */
}

/* Suits */
.card.HEARTS,
.card.DIAMONDS {
  color: #d40000;
}
.card.CLUBS,
.card.SPADES {
  color: #000;
}

.rank {
  font-size: 1.5rem;
  font-weight: bold;
}
.suit {
  font-size: 3rem;
  align-self: center;
}
.rank.bottom {
  transform: rotate(180deg);
  align-self: flex-end;
}

.card.back {
  background: linear-gradient(
    135deg,
    #b22222 25%,
    #800000 25%,
    #800000 50%,
    #b22222 50%,
    #b22222 75%,
    #800000 75%,
    #800000 100%
  );
  background-size: 20px 20px;
  border: 2px solid white;
}
.card.back .pattern {
  width: 100%;
  height: 100%;
} /* Placeholder for pattern */

/* Controls */
.controls {
  width: 100%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
  margin-bottom: 20px;
}

.chip-rack {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}

.chip-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 5px dashed white;
  background: #ffd700;
  color: #000;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
  transition: transform 0.1s;
}
.chip-btn:active {
  transform: scale(0.95);
}

.action-btn {
  padding: 15px 40px;
  font-size: 1.5rem;
  font-family: 'Cinzel', serif;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 200px;
  transition: all 0.2s;
}

.deal-btn {
  background: #ffd700;
  color: black;
  box-shadow: 0 0 20px #ffd70088;
}
.hit-btn {
  background: #28a745;
  color: white;
  margin-right: 20px;
}
.stand-btn {
  background: #dc3545;
  color: white;
}
.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Message Overlay */
.message-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  background: rgba(0, 0, 0, 0.8);
  padding: 40px;
  border-radius: 20px;
  border: 4px solid #ffd700;
  z-index: 100;
  animation: popIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.text-gold {
  color: #ffd700;
  text-shadow: 0 0 10px #ffd700;
}
.text-red {
  color: #fe2c55;
  text-shadow: 0 0 10px #fe2c55;
}

@keyframes popIn {
  from {
    transform: translate(-50%, -50%) scale(0.5);
    opacity: 0;
  }
  to {
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
  }
}

/* Vue Transitions */
.card-deal-enter-active,
.card-deal-leave-active {
  transition: all 0.5s ease;
}
.card-deal-enter-from {
  opacity: 0;
  transform: translateY(-100px) rotate(10deg);
}
.card-deal-leave-to {
  opacity: 0;
  transform: scale(0.5);
}
</style>
