<template>
  <div class="blackjack-table">
    <!-- Back Button -->
    <RouterLink to="/games" class="exit-btn" aria-label="Exit Game">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M18 6 6 18" />
        <path d="m6 6 12 12" />
      </svg>
    </RouterLink>

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
            v-for="(card, index) in dealerDisplayHandReal"
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
            {{ amount >= 1000 ? amount / 1000 + 'k' : amount }}
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
  if (game.value.status === 'PLAYER_TURN') {
    const visible = game.value.dealerHand.slice(0, 1) // Only first
    return calculateScore(visible)
  }
  return calculateScore(game.value.dealerHand)
})

const dealerDisplayHandReal = computed(() => {
  if (!game.value) return []
  if (game.value.status === 'PLAYER_TURN') {
    return [mapCard(game.value.dealerHand[0], 0)]
  }
  return game.value.dealerHand.map(mapCard)
})

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
  /* Fix: Full screen overlay to hide global nav */
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2000; /* High z-index to overlay nav */
  background: radial-gradient(circle at center, #35654d 0%, #1a3c2e 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  padding-top: 60px; /* Space for chips/exit */
  color: #fff;
  font-family: 'Roboto Mono', monospace;
  overflow: hidden;
  box-shadow: inset 0 0 100px rgba(0, 0, 0, 0.8);
}

.exit-btn {
  position: absolute;
  top: 15px;
  left: 15px;
  background: rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  padding: 10px;
  color: white;
  z-index: 10;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.exit-btn:hover {
  background: rgba(255, 0, 0, 0.7);
  border-color: red;
}

.table-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  max-width: 600px;
  margin-bottom: 20px;
}

.balance-chip,
.bet-chip {
  background: rgba(0, 0, 0, 0.6);
  padding: 8px 15px;
  border-radius: 30px;
  border: 1px solid #ffd700;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100px;
}

.label {
  font-size: 0.7rem;
  color: #aaa;
  text-transform: uppercase;
}
.value {
  font-size: 1rem;
  font-weight: bold;
  color: #ffd700;
}

.area {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1; /* Distribute space */
  justify-content: center;
  width: 100%;
}

.dealer-area {
  justify-content: flex-start;
}
.player-area {
  justify-content: flex-end;
}

.hand-score {
  margin-bottom: 10px;
  font-size: 1rem;
  background: rgba(0, 0, 0, 0.5);
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.cards-row {
  display: flex;
  justify-content: center;
  /* Overlap logic handled by margins */
  height: 140px;
}

.card {
  width: 90px;
  height: 130px;
  background: white;
  border-radius: 8px;
  color: black;
  position: relative;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 8px;
  transition: all 0.4s ease;
  margin-right: -40px; /* Default overlap */
  flex-shrink: 0;
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
  font-size: 1.2rem;
  font-weight: bold;
  line-height: 1;
}
.suit {
  font-size: 2.5rem;
  align-self: center;
  line-height: 1;
}
.rank.bottom {
  transform: rotate(180deg);
  align-self: flex-end;
}

.card.back {
  background: linear-gradient(
    135deg,
    #800000 25%,
    #500000 25%,
    #500000 50%,
    #800000 50%,
    #800000 75%,
    #500000 75%,
    #500000 100%
  );
  background-size: 10px 10px;
  border: 2px solid #fff;
}

/* Controls */
.controls {
  width: 100%;
  max-width: 500px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: center;
  margin-top: 20px;
  padding-bottom: 20px; /* Safe area */
}

.bet-controls,
.game-controls {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  width: 100%;
}
.game-controls {
  flex-direction: row;
  justify-content: center;
}

.chip-rack {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.chip-btn {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 3px dashed white;
  background: #ffd700;
  color: #000;
  font-weight: bold;
  font-size: 0.8rem;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}
.chip-btn:active {
  transform: scale(0.9);
}
.clear-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #ccc;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
}

.action-btn {
  padding: 12px 0;
  width: 140px;
  font-size: 1.2rem;
  font-family: 'Cinzel', serif;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.deal-btn {
  background: #ffd700;
  color: black;
  width: 80%;
  max-width: 300px;
}
.hit-btn {
  background: #28a745;
  color: white;
}
.stand-btn {
  background: #dc3545;
  color: white;
}
.action-btn:disabled {
  opacity: 0.5;
  filter: grayscale(1);
}

/* Message Overlay */
.message-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  background: rgba(0, 0, 0, 0.85);
  padding: 30px;
  border-radius: 16px;
  border: 2px solid #ffd700;
  z-index: 100;
  min-width: 200px;
  backdrop-filter: blur(5px);
}
.message-overlay h1 {
  margin: 0;
  font-size: 2rem;
}
.message-overlay p {
  margin: 10px 0 0;
  font-size: 1.2rem;
  color: #ffd700;
}

.text-gold {
  color: #ffd700;
  text-shadow: 0 0 10px #ffd700;
}
.text-red {
  color: #fe2c55;
  text-shadow: 0 0 10px #fe2c55;
}
.text-white {
  color: white;
}

/* Mobile Optimization */
@media (max-width: 600px) {
  .blackjack-table {
    padding: 0.5rem;
    padding-top: 50px;
  }
  .card {
    width: 70px;
    height: 100px;
    margin-right: -35px;
  }
  .rank {
    font-size: 1rem;
  }
  .suit {
    font-size: 1.8rem;
  }
  .cards-row {
    height: 110px;
  }
  .action-btn {
    width: 120px;
    font-size: 1rem;
    padding: 10px 0;
  }
  .deal-btn {
    width: 100%;
  }
  .chip-btn {
    width: 45px;
    height: 45px;
    font-size: 0.7rem;
  }
  .hand-score {
    font-size: 0.9rem;
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
