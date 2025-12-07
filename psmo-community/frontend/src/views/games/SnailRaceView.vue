<template>
  <div class="snail-race" ref="containerRef">
    <header class="page-head">
      <div>
        <p class="eyebrow">HTML5 Canvas</p>
        <h1>달팽이 경주</h1>
        <p class="lede">3마리 달팽이 중 우승을 맞추는 실시간 레이스. 포인트 베팅과 로그는 추후 연동됩니다.</p>
      </div>
      <div class="head-actions">
        <RouterLink to="/games" class="ghost-link">← 게임 허브로</RouterLink>
      </div>
    </header>

    <section class="control-panel">
      <div class="pick-row">
        <p class="label">내가 응원할 달팽이</p>
        <div class="chips">
          <button
            v-for="snail in snails"
            :key="snail.id"
            class="chip"
            :class="{ active: selectedSnailId === snail.id }"
            @click="selectSnail(snail.id)"
          >
            <span class="dot" :style="{ background: snail.color }"></span>
            {{ snail.name }}
          </button>
        </div>
      </div>

      <div class="cta-row">
        <button class="primary" :disabled="raceState === 'running'" @click="startRace">
          {{ raceState === 'running' ? '달리는 중...' : '경주 시작' }}
        </button>
        <button class="ghost" :disabled="raceState === 'running'" @click="resetRace">
          다시 세팅
        </button>
        <span class="status" :class="raceState">{{ statusText }}</span>
      </div>
    </section>

    <section class="canvas-wrap">
      <canvas ref="canvasRef" class="race-canvas"></canvas>
    </section>

    <section class="info">
      <div class="card">
        <h3>룰</h3>
        <ul>
          <li>3마리 달팽이 중 하나를 선택하고 경주를 시작합니다.</li>
          <li>달팽이마다 기본 속도는 조금씩 다르며, 매 프레임 무작위 가속이 붙습니다.</li>
          <li>결승선을 먼저 통과한 달팽이가 우승합니다. 무승부 시 공동 1위 처리합니다.</li>
        </ul>
      </div>
      <div class="card">
        <h3>향후 연동</h3>
        <ul>
          <li>포인트 베팅/정산, 서버 검증, 리플레이 로그 저장</li>
          <li>일일/주간 쿨다운 및 공정성 로그(난수 시드) 공개</li>
          <li>멀티플레이 동기화 및 부정 방지 캡차 적용</li>
        </ul>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'

type RaceState = 'idle' | 'running' | 'finished'

type Snail = {
  id: number
  name: string
  color: string
  baseSpeed: number
  position: number
}

const canvasRef = ref<HTMLCanvasElement | null>(null)
const containerRef = ref<HTMLElement | null>(null)
const trackLength = ref(520)
const snails = ref<Snail[]>([
  { id: 1, name: '루나', color: '#6366f1', baseSpeed: 1.05, position: 0 },
  { id: 2, name: '모코', color: '#10b981', baseSpeed: 1.0, position: 0 },
  { id: 3, name: '보라', color: '#f59e0b', baseSpeed: 0.95, position: 0 },
])
const selectedSnailId = ref<number>(1)
const raceState = ref<RaceState>('idle')
const statusText = ref('달팽이를 고르고 시작하세요')
const winnerIds = ref<number[]>([])
const animationId = ref<number | null>(null)

const laneHeight = 70
const margin = 40

const selectSnail = (id: number) => {
  if (raceState.value === 'running') return
  selectedSnailId.value = id
}

const resetRace = () => {
  snails.value = snails.value.map((s) => ({ ...s, position: 0 }))
  winnerIds.value = []
  statusText.value = '달팽이를 고르고 시작하세요'
  raceState.value = 'idle'
  draw()
}

const setCanvasSize = () => {
  const width = Math.min(Math.max(containerRef.value?.clientWidth ?? 360, 340), 980)
  trackLength.value = width - margin * 2
  const canvas = canvasRef.value
  if (!canvas) return
  canvas.width = width
  canvas.height = laneHeight * snails.value.length + margin
  draw()
}

const startRace = () => {
  if (raceState.value === 'running') return
  if (!selectedSnailId.value) {
    statusText.value = '달팽이를 선택하세요'
    return
  }
  resetRace()
  raceState.value = 'running'
  statusText.value = '경주 중...'
  tick()
}

const finishRace = () => {
  if (raceState.value !== 'running') return
  raceState.value = 'finished'
  const maxPos = Math.max(...snails.value.map((s) => s.position))
  winnerIds.value = snails.value.filter((s) => s.position >= maxPos - 1).map((s) => s.id)
  const userWon = winnerIds.value.includes(selectedSnailId.value)
  statusText.value = userWon ? '축하! 내가 고른 달팽이가 1등' : '아쉽지만 다음 기회에'
}

const tick = () => {
  const canvas = canvasRef.value
  if (!canvas) return

  snails.value = snails.value.map((s) => {
    const burst = Math.random() * 0.6
    const drift = Math.random() * 0.25
    const delta = s.baseSpeed + burst - drift * 0.3
    const next = Math.min(trackLength.value, s.position + delta)
    return { ...s, position: next }
  })

  draw()

  const isFinished = snails.value.some((s) => s.position >= trackLength.value)
  if (isFinished) {
    finishRace()
    return
  }

  animationId.value = window.requestAnimationFrame(tick)
}

const draw = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  ctx.clearRect(0, 0, canvas.width, canvas.height)

  snails.value.forEach((snail, idx) => {
    const y = margin / 2 + idx * laneHeight + laneHeight / 2
    ctx.strokeStyle = '#e5e7eb'
    ctx.lineWidth = 2
    ctx.beginPath()
    ctx.moveTo(margin, y)
    ctx.lineTo(canvas.width - margin, y)
    ctx.stroke()

    ctx.strokeStyle = '#9ca3af'
    ctx.setLineDash([8, 6])
    ctx.beginPath()
    ctx.moveTo(canvas.width - margin, y - 20)
    ctx.lineTo(canvas.width - margin, y + 20)
    ctx.stroke()
    ctx.setLineDash([])

    const x = margin + snail.position
    ctx.fillStyle = snail.color
    ctx.beginPath()
    ctx.arc(x, y, 14, 0, Math.PI * 2)
    ctx.fill()

    ctx.fillStyle = '#111827'
    ctx.font = '12px "Inter", system-ui'
    ctx.fillText(snail.name, x - 16, y - 18)
  })
}

const handleResize = () => setCanvasSize()

onMounted(() => {
  setCanvasSize()
  window.addEventListener('resize', handleResize)
  draw()
})

onBeforeUnmount(() => {
  if (animationId.value) window.cancelAnimationFrame(animationId.value)
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.snail-race {
  background: #f8f9fb;
  min-height: calc(100vh - 60px);
  padding: 1.25rem 1rem 2rem;
}

.page-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1rem;
}

.eyebrow {
  margin: 0;
  font-size: 0.85rem;
  font-weight: 700;
  color: #6366f1;
}

h1 {
  margin: 0.15rem 0;
  font-size: 1.6rem;
  color: #0f172a;
}

.lede {
  margin: 0;
  color: #475569;
  line-height: 1.5;
}

.head-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.ghost-link {
  color: #4b5563;
  text-decoration: none;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 0.55rem 0.8rem;
  background: #fff;
}

.control-panel {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
  margin-bottom: 1rem;
  display: grid;
  gap: 0.75rem;
}

.pick-row {
  display: grid;
  gap: 0.4rem;
}

.label {
  margin: 0;
  font-weight: 700;
  color: #111827;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 0.55rem 0.8rem;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  font-weight: 700;
  color: #111827;
  transition: transform 0.12s ease, box-shadow 0.12s ease, border-color 0.12s ease;
}

.chip .dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.chip.active {
  border-color: #6366f1;
  box-shadow: 0 8px 18px rgba(99, 102, 241, 0.16);
  transform: translateY(-1px);
}

.cta-row {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  align-items: center;
}

.primary,
.ghost {
  border: none;
  border-radius: 10px;
  padding: 0.75rem 1.1rem;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.12s ease, box-shadow 0.12s ease, background 0.12s ease;
}

.primary {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  box-shadow: 0 10px 22px rgba(99, 102, 241, 0.24);
}

.primary:disabled,
.ghost:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.ghost {
  background: #fff;
  border: 1px solid #e5e7eb;
  color: #4b5563;
}

.primary:hover:not(:disabled),
.ghost:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.status {
  font-weight: 700;
  color: #4b5563;
}

.status.finished {
  color: #059669;
}

.status.running {
  color: #6366f1;
}

.canvas-wrap {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 0.75rem;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
  margin-bottom: 1rem;
}

.race-canvas {
  width: 100%;
  display: block;
}

.info {
  display: grid;
  gap: 0.75rem;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.04);
}

.card h3 {
  margin: 0 0 0.5rem;
  color: #0f172a;
}

.card ul {
  margin: 0;
  padding-left: 1rem;
  color: #4b5563;
  line-height: 1.5;
}

@media (min-width: 760px) {
  .snail-race {
    padding: 1.5rem 1.25rem 2.5rem;
    max-width: 960px;
    margin: 0 auto;
  }
}
</style>
