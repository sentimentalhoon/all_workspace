<template>
  <div class="snail-race-2">
    <!-- Exit Button -->
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

    <!-- Game Container -->
    <div class="game-container" ref="containerRef">
      <!-- Top Info Bar -->
      <div class="header-bar">
        <div class="balance-info">
          <span class="label">MY POINTS</span>
          <span class="value">{{ balance.toLocaleString() }}</span>
        </div>
        <div class="status-badge" :class="raceState">
          {{ statusText }}
        </div>
      </div>

      <!-- Canvas Area -->
      <div class="canvas-wrapper">
        <canvas ref="canvasRef"></canvas>

        <!-- Win Overlay -->
        <div v-if="showWinOverlay" class="win-overlay">
          <div class="winner-card" :class="{ 'user-won': userWon }">
            <h1>{{ userWon ? 'VICTORY!' : 'FINISHED' }}</h1>
            <div class="winner-snail">
              <span class="crown">👑</span>
              <span class="name" :style="{ color: winnerSnail?.color }">{{
                winnerSnail?.name
              }}</span>
            </div>
            <p class="payout" v-if="serverResult && serverResult.payout > 0">
              Your Payout: <span class="gold">+{{ serverResult.payout.toLocaleString() }}</span>
            </p>
            <p class="payout" v-else-if="userWon">Calculating Payout...</p>
            <button class="retry-btn" @click="resetRace">PLAY AGAIN</button>
          </div>
        </div>
      </div>

      <!-- Controls Area -->
      <div class="controls-area">
        <!-- Betting Panel (Visible when Idle) -->
        <div v-if="raceState === 'idle' || raceState === 'finished'" class="betting-panel">
          <div class="snail-selector">
            <p class="label">PICK YOUR RACER</p>
            <div class="snails-grid">
              <button
                v-for="snail in snails"
                :key="snail.id"
                class="snail-btn"
                :class="{ selected: selectedSnailId === snail.id }"
                @click="selectSnail(snail.id)"
                :style="{ '--snail-color': snail.color }"
              >
                <div class="snail-avatar" :style="{ background: snail.color }">🐌</div>
                <div class="snail-info">
                  <span class="name">{{ snail.name }}</span>
                  <span class="odds">x2.0</span>
                </div>
              </button>
            </div>
          </div>

          <div class="bet-input-row">
            <p class="label">BET AMOUNT</p>
            <div class="chips-row">
              <button
                v-for="amt in [100, 500, 1000, 5000]"
                :key="amt"
                class="chip"
                @click="addBet(amt)"
              >
                {{ amt >= 1000 ? amt / 1000 + 'k' : amt }}
              </button>
              <button class="clear-btn" @click="betAmount = 0">CLEAR</button>
            </div>
            <div class="current-bet">
              TOTAL BET: <span class="gold">{{ betAmount.toLocaleString() }}</span>
            </div>
          </div>

          <button class="start-btn" :disabled="!canStart" @click="startRace">
            {{ startBtnLabel }}
          </button>
        </div>

        <!-- Running Status (Visible when Running) -->
        <div v-else class="running-panel">
          <div class="leaderboard">
            <div v-for="(s, i) in sortedSnails" :key="s.id" class="rank-row">
              <span class="rank">#{{ i + 1 }}</span>
              <span class="name" :style="{ color: s.color }">{{ s.name }}</span>
              <div class="progress-bar">
                <div
                  class="fill"
                  :style="{ width: (s.position / trackLength) * 100 + '%', background: s.color }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import type { SnailRaceResultResponse, SnailRunner } from '@/types/games'
import { completeSnailRace, startSnailRace } from '@/utils/gameApi'
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

// Types
type RaceState = 'idle' | 'running' | 'finished'
type Snail = SnailRunner & { position: number; rank: number }

// Store & Formatting
const auth = useAuthStore()
const balance = computed(() => auth.user?.score ?? 0)

// Canvas & Subs
const canvasRef = ref<HTMLCanvasElement | null>(null)
const containerRef = ref<HTMLElement | null>(null)
const animationId = ref<number | null>(null)
const frameCount = ref(0)

// Game State
const raceState = ref<RaceState>('idle')
const snails = ref<Snail[]>([
  { id: 1, name: 'Turbo', color: '#ff0055', baseSpeed: 1.0, position: 0, rank: 0 },
  { id: 2, name: 'Flash', color: '#00ffaa', baseSpeed: 1.0, position: 0, rank: 0 },
  { id: 3, name: 'Bolt', color: '#ffff00', baseSpeed: 1.0, position: 0, rank: 0 },
])
const selectedSnailId = ref<number>(1)
const betAmount = ref(100)
const trackLength = ref(800) // virtual pixels

// Snail Metadata
const statusText = ref('READY TO RACE')
const serverResult = ref<SnailRaceResultResponse | null>(null)
const raceMeta = ref<{ id: string | null; seed: string | null }>({ id: null, seed: null })
const rng = ref<() => number>(() => Math.random())

// Particles
interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  life: number
  color: string
}
let particles: Particle[] = []

// Computeds
const canStart = computed(() => {
  return raceState.value === 'idle' && betAmount.value > 0 && betAmount.value <= balance.value
})
const startBtnLabel = computed(() => {
  if (betAmount.value > balance.value) return 'INSUFFICIENT FUNDS'
  if (betAmount.value === 0) return 'PLACE A BET'
  return 'START RACE'
})
const sortedSnails = computed(() => [...snails.value].sort((a, b) => b.position - a.position))
const winnerSnail = computed(() => {
  if (raceState.value !== 'finished') return null
  // Highest position is winner
  return [...snails.value].sort((a, b) => b.position - a.position)[0]
})
const userWon = computed(() => winnerSnail.value?.id === selectedSnailId.value)
const showWinOverlay = computed(() => raceState.value === 'finished')

// Camera Shake
let shakeIntensity = 0

// Methods
const addBet = (amt: number) => {
  if (betAmount.value + amt <= balance.value) betAmount.value += amt
}
const selectSnail = (id: number) => (selectedSnailId.value = id)

const resetRace = () => {
  if (animationId.value) {
    cancelAnimationFrame(animationId.value)
    animationId.value = null
  }
  raceState.value = 'idle'

  // Explicitly clear canvas to remove any artifacts immediately
  const canvas = canvasRef.value
  const ctx = canvas?.getContext('2d')
  if (canvas && ctx) {
    ctx.setTransform(1, 0, 0, 1, 0, 0)
    ctx.clearRect(0, 0, canvas.width, canvas.height)
  }

  // Deep reset
  snails.value = snails.value.map((s) => ({ ...s, position: 0, rank: 0 }))
  particles = []
  shakeIntensity = 0
  statusText.value = 'READY TO RACE'
  betAmount.value = 100
  serverResult.value = null

  // Double raf to ensure UI paint first then canvas draw
  requestAnimationFrame(() => {
    requestAnimationFrame(draw)
  })
}

// PRNG (Copy from old impl)
const hashSeed = (str: string) => {
  let h = 1779033703 ^ str.length
  for (let i = 0; i < str.length; i++) {
    h = Math.imul(h ^ str.charCodeAt(i), 3432918353)
    h = (h << 13) | (h >>> 19)
  }
  return () => h >>> 0
}
const mulberry32 = (a: number) => () => {
  let t = (a += 0x6d2b79f5)
  t = Math.imul(t ^ (t >>> 15), t | 1)
  t ^= t + Math.imul(t ^ (t >>> 7), t | 61)
  return ((t ^ (t >>> 14)) >>> 0) / 4294967296
}
const createRng = (seed: string) => mulberry32(hashSeed(seed)())

// Game Loop
const startRace = async () => {
  if (!canStart.value) return

  // Optimistic deduct
  auth.updateScore(auth.user!.score - betAmount.value)
  statusText.value = 'CONNECTING...'

  try {
    const clientSeed = Math.random().toString(36).substring(7)
    const res = await startSnailRace({
      snailId: selectedSnailId.value,
      betAmount: betAmount.value,
      clientSeed,
    })

    // Setup Game
    rng.value = createRng(res.seed)
    raceMeta.value = { id: res.raceId, seed: res.seed }
    snails.value.forEach((s) => (s.position = 0)) // Reset pos
    // If backend changed snail order/ids, we should map them, but for now we assume static IDs 1,2,3

    raceState.value = 'running'
    statusText.value = 'RACE IN PROGRESS'
    frameCount.value = 0
    shakeIntensity = 0
    particles = []

    tick()
  } catch (e: any) {
    alert(e.message || 'Failed to start race')
    auth.updateScore(auth.user!.score + betAmount.value) // Refund
    statusText.value = 'ERROR'
  }
}

const tick = () => {
  if (raceState.value !== 'running') return

  const random = rng.value
  let finished = false

  snails.value.forEach((s) => {
    const burst = random() * 1.5 // Increased speed for excitement
    const drift = random() * 0.2
    const delta = s.baseSpeed + burst - drift
    const prevPos = s.position
    s.position += delta

    // Dust particles
    if (Math.random() > 0.7) {
      particles.push({
        x: 50 + s.position, // offset from start
        y: getLaneY(s.id) + 20, // bottom of snail
        vx: -1 - Math.random(),
        vy: -1 + Math.random() * 2,
        life: 1.0,
        color: 'rgba(255,255,255,0.5)',
      })
    }
  })

  // Camera Shake if leader is close to finish
  const leaderPos = Math.max(...snails.value.map((s) => s.position))
  if (leaderPos > trackLength.value * 0.9) {
    shakeIntensity = 2 + (leaderPos - trackLength.value * 0.9) / 20
  }

  frameCount.value++

  // Check finish
  if (snails.value.some((s) => s.position >= trackLength.value)) {
    finished = true
    finishRace()
  }

  draw()

  if (!finished) {
    animationId.value = requestAnimationFrame(tick)
  }
}

const finishRace = async () => {
  raceState.value = 'finished'
  statusText.value = 'FINISHING...'

  if (raceMeta.value.id) {
    try {
      const durationMs = 0 // Mock, backend mainly verifies sequence
      const winnerIds = sortedSnails.value
        .filter((s) => s.position >= trackLength.value)
        .map((s) => s.id)

      const res = await completeSnailRace(raceMeta.value.id, {
        raceId: raceMeta.value.id,
        winnerIds: [winnerSnail.value!.id],
        durationMs,
        frames: frameCount.value,
        seed: raceMeta.value.seed!,
        betAmount: betAmount.value,
        betSnailId: selectedSnailId.value,
        clientLog: { positions: snails.value.map((s) => Math.round(s.position)) },
      })

      // Guard: If user already reset the race, ignore this result to prevent state corruption
      if (raceState.value !== 'finished') return

      serverResult.value = res
      if (res.balance) auth.updateScore(res.balance)
      statusText.value = 'VERIFIED'
    } catch (e) {
      if (raceState.value !== 'finished') return
      console.error(e)
      statusText.value = 'NETWORK ERROR'
    }
  }
}

// Drawing
const getLaneY = (id: number) => {
  const canvas = canvasRef.value
  if (!canvas) return 0
  const laneHeight = canvas.height / 3
  return (id - 1) * laneHeight + laneHeight / 2
}

const draw = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  // Responsive size match
  const rect = canvas.getBoundingClientRect()
  canvas.width = rect.width
  canvas.height = rect.height

  // Logical track scaling
  // We map trackLength (800) to Canvas Width - Margins
  const marginX = 80
  const scale = (canvas.width - marginX * 2) / trackLength.value

  // Clear & Background
  // Reset transform to handle shake cleanups or resizing artifacts
  ctx.setTransform(1, 0, 0, 1, 0, 0)
  ctx.fillStyle = '#111'
  ctx.fillRect(0, 0, canvas.width, canvas.height)

  // Shake
  ctx.save()
  if (shakeIntensity > 0) {
    const dx = (Math.random() - 0.5) * shakeIntensity
    const dy = (Math.random() - 0.5) * shakeIntensity
    ctx.translate(dx, dy)
  }

  // Draw Tracks
  snails.value.forEach((s) => {
    const y = getLaneY(s.id)

    // Lane Line
    ctx.beginPath()
    ctx.moveTo(marginX, y)
    ctx.lineTo(canvas.width - marginX, y)
    ctx.strokeStyle = 'rgba(255,255,255,0.1)'
    ctx.lineWidth = 2
    ctx.stroke()

    // Finish Line
    ctx.beginPath()
    ctx.moveTo(canvas.width - marginX, 0)
    ctx.lineTo(canvas.width - marginX, canvas.height)
    ctx.strokeStyle = '#ffd700'
    ctx.lineWidth = 4
    ctx.stroke()

    // Snail
    const sx = marginX + s.position * scale
    drawSnail(ctx, sx, y, s.color, s.name)
  })

  // Particles
  particles.forEach((p, i) => {
    p.x += p.vx
    p.y += p.vy
    p.life -= 0.02
    if (p.life <= 0) {
      particles.splice(i, 1)
      return
    }
    const px = marginX + p.x * scale // simplified mapping, actually p.x is 0..800
    // Wait, p.x is already tracking position value.
    // If I map p.x to screen, I need to use 'scale' scaling but relative to marginX
    // Actually p.x was pushed as 'position'. So yes:
    const drawX = marginX + (p.x - 50) * scale // quick fix for offset

    ctx.fillStyle = p.color
    ctx.globalAlpha = p.life
    ctx.beginPath()
    ctx.arc(drawX, p.y, 2, 0, Math.PI * 2)
    ctx.fill()
  })
  ctx.restore()
}

const drawSnail = (
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  color: string,
  name: string,
) => {
  // Glow
  ctx.shadowColor = color
  ctx.shadowBlur = 15

  // Body
  ctx.fillStyle = color
  ctx.beginPath()
  ctx.arc(x, y, 20, 0, Math.PI * 2)
  ctx.fill()

  // Reset Glow
  ctx.shadowBlur = 0

  // Emoji
  ctx.font = '24px Arial'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText('🐌', x, y + 2)

  // Name Label
  ctx.fillStyle = '#fff'
  ctx.font = 'bold 12px Roboto Mono'
  ctx.fillText(name, x, y - 30)
}

onMounted(() => {
  if (containerRef.value) {
    draw() // Initial draw
  }
  window.addEventListener('resize', draw)
})

onBeforeUnmount(() => {
  if (animationId.value) cancelAnimationFrame(animationId.value)
  window.removeEventListener('resize', draw)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@700&family=Roboto+Mono:wght@500;700&display=swap');

.snail-race-2 {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, #1e1e2e 0%, #000000 100%);
  color: #fff;
  font-family: 'Roboto Mono', monospace;
  z-index: 2000;
  display: flex;
  flex-direction: column;
}

.exit-btn {
  position: absolute;
  top: 15px;
  left: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  padding: 8px;
  color: #fff;
  z-index: 50;
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
}

.game-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 60px 20px 20px;
  max-width: 800px;
  width: 100%;
  margin: 0 auto;
}

.header-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.balance-info {
  display: flex;
  flex-direction: column;
}
.label {
  font-size: 0.7rem;
  color: #888;
  letter-spacing: 2px;
}
.value {
  font-size: 1.2rem;
  font-weight: bold;
  color: #ffd700;
}
.status-badge {
  background: #333;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
  height: fit-content;
  border: 1px solid #555;
}
.status-badge.running {
  border-color: #00ffaa;
  color: #00ffaa;
  animate: pulse 1s infinite;
}
.status-badge.finished {
  border-color: #ffd700;
  color: #ffd700;
}

.canvas-wrapper {
  position: relative;
  flex: 1;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: inset 0 0 50px #000;
}
canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.win-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s;
}
.winner-card {
  background: #111;
  border: 2px solid #ffd700;
  padding: 30px;
  border-radius: 20px;
  text-align: center;
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.3);
  transform: scale(0.8);
  animation: popIn 0.3s forwards;
}
.winner-card h1 {
  font-family: 'Cinzel';
  color: #ffd700;
  margin-bottom: 20px;
  font-size: 2rem;
}
.winner-snail {
  font-size: 3rem;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.winner-snail .name {
  font-size: 1.5rem;
  font-weight: bold;
  text-transform: uppercase;
}
.retry-btn {
  background: #ffd700;
  color: #000;
  padding: 15px 40px;
  border: none;
  font-weight: bold;
  font-family: 'Cinzel';
  margin-top: 20px;
  cursor: pointer;
  border-radius: 5px;
}

.controls-area {
  background: rgba(0, 0, 0, 0.5);
  border-radius: 20px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.snails-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 10px;
}
.snail-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 2px solid transparent;
  border-radius: 10px;
  padding: 15px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  transition: all 0.2s;
  color: #fff;
}
.snail-btn.selected {
  background: rgba(255, 255, 255, 0.1);
  border-color: var(--snail-color);
  box-shadow: 0 0 15px var(--snail-color);
}
.snail-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}
.snail-info {
  display: flex;
  flex-direction: column;
  font-size: 0.8rem;
}
.odds {
  color: #888;
  font-size: 0.7rem;
}

.bet-input-row {
  margin-top: 20px;
}
.chips-row {
  display: flex;
  gap: 10px;
  margin: 10px 0;
  overflow-x: auto;
  padding-bottom: 5px;
}
.chip {
  background: #222;
  border: 1px solid #ffd700;
  color: #ffd700;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  flex-shrink: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.8rem;
}
.start-btn {
  width: 100%;
  background: #00ffaa;
  color: #000;
  font-family: 'Cinzel';
  font-size: 1.5rem;
  font-weight: bold;
  padding: 15px;
  border: none;
  border-radius: 10px;
  margin-top: 20px;
  cursor: pointer;
}
.start-btn:disabled {
  background: #333;
  color: #555;
  cursor: not-allowed;
}

.leaderboard {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.rank-row {
  display: flex;
  align-items: center;
  gap: 15px;
}
.progress-bar {
  flex: 1;
  height: 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
  overflow: hidden;
}
.fill {
  height: 100%;
  transition: width 0.1s linear;
}

@keyframes popIn {
  from {
    transform: scale(0.5);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

@media (max-width: 600px) {
  .game-container {
    padding-top: 50px;
  }
  .snail-btn {
    padding: 10px;
  }
  .snail-avatar {
    width: 35px;
    height: 35px;
    font-size: 1.2rem;
  }
  .chips-row {
    flex-wrap: wrap;
    justify-content: center;
  }
  .start-btn {
    font-size: 1.2rem;
  }
}
</style>
