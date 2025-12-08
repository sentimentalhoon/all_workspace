<template>
  <div class="snail-race" ref="containerRef">
    <header class="page-head">
      <div>
        <p class="eyebrow">HTML5 Canvas</p>
        <h1>달팽이 경주</h1>
        <p class="lede">
          3마리 달팽이 중 우승을 맞추는 실시간 레이스. 포인트 베팅과 로그는 추후 연동됩니다.
        </p>
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

      <div class="bet-row">
        <div class="bet-header">
          <p class="label">베팅 포인트</p>
          <span class="balance">보유 {{ formatPoints(balance) }}p</span>
        </div>
        <div class="bet-input">
          <input
            v-model.number="betAmount"
            type="number"
            :min="betLimits.minBet"
            :max="betLimits.maxBet"
            :step="betLimits.step"
          />
          <div class="bet-hint">
            <span
              >최소 {{ formatPoints(betLimits.minBet) }}p · 최대
              {{ formatPoints(betLimits.maxBet) }}p</span
            >
            <span v-if="betLimits.cooldownSeconds">
              기본 쿨다운 {{ betLimits.cooldownSeconds }}초
            </span>
            <span v-if="cooldownRemaining !== null" class="cooldown-chip">
              남은 쿨다운 {{ cooldownRemaining }}초
            </span>
          </div>
        </div>
        <p v-if="betError" class="error">{{ betError }}</p>
      </div>

      <div class="cta-row">
        <button
          class="primary"
          :disabled="!canStart || isInCooldown"
          :title="isInCooldown ? `쿨다운 ${cooldownRemaining}초` : ''"
          @click="startRace"
        >
          {{ startButtonLabel }}
        </button>
        <button class="ghost" :disabled="!canReset" @click="resetRace">다시 세팅</button>
        <span class="status" :class="raceState">{{ statusText }}</span>
      </div>

      <p v-if="serverError" class="server-error">{{ serverError }}</p>
    </section>

    <section class="canvas-wrap">
      <div class="meta-bar">
        <div class="meta">
          <p>Race ID</p>
          <strong>{{ raceMeta.id ?? '로컬 모드' }}</strong>
        </div>
        <div class="meta">
          <p>Seed</p>
          <strong class="mono">{{ raceMeta.seed ?? '-' }}</strong>
        </div>
        <div class="meta">
          <p>검증 상태</p>
          <strong :class="{ verified: serverResult?.verified }">
            {{
              serverResult
                ? serverResult.verified
                  ? '서버 검증 완료'
                  : '검증 필요'
                : raceMeta.id
                  ? '서버 기록 대기'
                  : '오프라인 진행'
            }}
          </strong>
        </div>
      </div>
      <canvas ref="canvasRef" class="race-canvas"></canvas>
      <div v-if="raceMeta.fairnessHash" class="fairness">
        <p>Fairness Hash</p>
        <div class="fairness-row">
          <code>{{ raceMeta.fairnessHash }}</code>
          <button class="ghost tiny" @click="copyMeta(raceMeta.fairnessHash, 'Hash')">복사</button>
        </div>
      </div>
      <div v-if="copyStatus" class="copy-status">{{ copyStatus }}</div>
      <div v-if="serverResult?.serverNote" class="server-note">{{ serverResult.serverNote }}</div>
      <div v-if="raceMeta.id && raceState === 'finished'" class="report-row">
        <button
          class="ghost"
          :disabled="isReporting || serverResult?.verified"
          @click="retryReport"
        >
          {{ isReporting ? '보고 중...' : serverResult?.verified ? '검증 완료' : '결과 다시 보고' }}
        </button>
      </div>
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
        <h3>서버 연동 현황</h3>
        <ul>
          <li>베팅 금액·선택 달팽이 정보를 서버에 전송해 검증합니다.</li>
          <li>서버에서 내려준 시드로 애니메이션을 재현하고 해시를 표시합니다.</li>
          <li>경주 종료 시 결과를 다시 서버에 보고하여 포인트 정산을 요청합니다.</li>
        </ul>
      </div>
      <div class="card">
        <h3>최근 결과</h3>
        <ul>
          <li>Race ID: {{ raceMeta.id ?? '없음' }}</li>
          <li>Seed: {{ raceMeta.seed ?? '-' }}</li>
          <li>
            상태:
            {{
              serverResult
                ? serverResult.verified
                  ? '서버 검증 완료'
                  : '검증 필요'
                : raceMeta.id
                  ? '기록 대기'
                  : '오프라인'
            }}
          </li>
          <li v-if="serverResult">지급/차감: {{ formatPoints(serverResult.payout) }}p</li>
          <li v-if="serverResult">새 잔액: {{ formatPoints(serverResult.balance) }}p</li>
        </ul>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

import { useToast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth'
import type {
  SnailRaceLimits,
  SnailRaceResultResponse,
  SnailRaceStartResponse,
  SnailRunner,
} from '@/types/games'
import { GameApiError, completeSnailRace, startSnailRace } from '@/utils/gameApi'

type RaceState = 'idle' | 'running' | 'finished'
type Snail = SnailRunner & { position: number }

const auth = useAuthStore()
const balance = computed(() => auth.user?.score ?? 0)
const nf = new Intl.NumberFormat('ko-KR')
const formatPoints = (value: number) => nf.format(value)

const canStart = computed(() => raceState.value !== 'running' && !isStarting.value)
const canReset = computed(
  () => raceState.value !== 'running' && !isStarting.value && !isReporting.value,
)
const isInCooldown = computed(() => cooldownRemaining.value !== null && cooldownRemaining.value > 0)
const startButtonLabel = computed(() => {
  if (isStarting.value) return '서버 확인 중...'
  if (raceState.value === 'running') return '달리는 중...'
  if (isInCooldown.value) return `쿨다운 ${cooldownRemaining.value ?? ''}초`
  return '경주 시작'
})

const canvasRef = ref<HTMLCanvasElement | null>(null)
const containerRef = ref<HTMLElement | null>(null)
const trackLength = ref(520)
const snails = ref<Snail[]>([
  // baseSpeed를 동일하게 맞춰 기본 승률 편향을 제거하고, 랜덤 요소에만 의존하도록 맞춘다.
  { id: 1, name: '루나', color: '#6366f1', baseSpeed: 1.0, position: 0 },
  { id: 2, name: '모코', color: '#10b981', baseSpeed: 1.0, position: 0 },
  { id: 3, name: '보라', color: '#f59e0b', baseSpeed: 1.0, position: 0 },
])

const selectedSnailId = ref<number>(1)
const raceState = ref<RaceState>('idle')
const statusText = ref('달팽이를 고르고 시작하세요')
const winnerIds = ref<number[]>([])
const animationId = ref<number | null>(null)
const serverResult = ref<SnailRaceResultResponse | null>(null)
const serverError = ref<string | null>(null)
const raceMeta = ref<{ id: string | null; seed: string | null; fairnessHash: string | null }>({
  id: null,
  seed: null,
  fairnessHash: null,
})
const betLimits = ref<SnailRaceLimits>({ minBet: 100, maxBet: 5000, step: 50 })
const betAmount = ref(100)
const betError = ref<string | null>(null)
const isStarting = ref(false)
const isReporting = ref(false)
const cooldownRemaining = ref<number | null>(null)
let cooldownTimer: number | null = null
const frameCount = ref(0)
const raceStartedAt = ref<number | null>(null)
const rng = ref<() => number>(() => Math.random())
const copyStatus = ref<string | null>(null)
const { toast, showToast, hideToast } = useToast()

const laneHeight = 70
const margin = 40

const hashSeed = (str: string) => {
  let h = 1779033703 ^ str.length
  for (let i = 0; i < str.length; i += 1) {
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
const randomSeed = () => {
  const c = globalThis.crypto
  if (c?.randomUUID) return c.randomUUID()
  if (c?.getRandomValues) {
    const arr = new Uint32Array(4)
    c.getRandomValues(arr)
    return Array.from(arr)
      .map((n) => n.toString(16).padStart(8, '0'))
      .join('-')
  }
  return Math.random().toString(36).slice(2)
}

const selectSnail = (id: number) => {
  if (raceState.value === 'running') return
  selectedSnailId.value = id
}

const clearCooldownTimer = () => {
  if (cooldownTimer) {
    window.clearInterval(cooldownTimer)
    cooldownTimer = null
  }
}

const resetPositions = () => {
  snails.value = snails.value.map((s) => ({ ...s, position: 0 }))
  winnerIds.value = []
  frameCount.value = 0
  raceStartedAt.value = null
}

const resetRace = () => {
  if (animationId.value) window.cancelAnimationFrame(animationId.value)
  animationId.value = null
  resetPositions()
  raceMeta.value = { id: null, seed: null, fairnessHash: null }
  serverResult.value = null
  serverError.value = null
  statusText.value = '달팽이를 고르고 시작하세요'
  raceState.value = 'idle'
  cooldownRemaining.value = null
  clearCooldownTimer()
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

const validateBet = () => {
  betError.value = null
  const { minBet, maxBet, step } = betLimits.value

  if (betAmount.value < minBet) {
    betError.value = `최소 베팅은 ${minBet.toLocaleString()}p 입니다.`
    return false
  }
  if (betAmount.value > maxBet) {
    betError.value = `최대 베팅은 ${maxBet.toLocaleString()}p 입니다.`
    return false
  }
  if (betAmount.value % step !== 0) {
    betError.value = `${step}p 단위로 베팅할 수 있습니다.`
    return false
  }
  if (betAmount.value > balance.value) {
    betError.value = '보유 포인트를 초과했습니다.'
    return false
  }
  return true
}

const applyServerStart = (start: SnailRaceStartResponse) => {
  snails.value = start.snails.map((s) => ({ ...s, position: 0 }))
  trackLength.value = start.trackLength ?? trackLength.value
  betLimits.value = start.limits ?? betLimits.value
  raceMeta.value = {
    id: start.raceId,
    seed: start.seed,
    fairnessHash: start.fairnessHash ?? null,
  }
  rng.value = createRng(start.seed)
}

const startRace = async () => {
  if (raceState.value === 'running') return
  if (!selectedSnailId.value) {
    statusText.value = '달팽이를 선택하세요'
    return
  }
  if (!validateBet()) return

  serverError.value = null
  serverResult.value = null
  resetPositions()
  cooldownRemaining.value = null
  clearCooldownTimer()

  const clientSeed = randomSeed()
  let started = false
  isStarting.value = true
  try {
    const start = await startSnailRace({
      snailId: selectedSnailId.value,
      betAmount: betAmount.value,
      clientSeed,
    })
    applyServerStart(start)
    started = true
  } catch (err) {
    if (err instanceof GameApiError) {
      if (err.code === 'COOLDOWN' && typeof err.remainingSeconds === 'number') {
        cooldownRemaining.value = Math.max(0, Math.floor(err.remainingSeconds))
        serverError.value = `쿨다운 ${cooldownRemaining.value}초 남았습니다.`
        clearCooldownTimer()
        cooldownTimer = window.setInterval(() => {
          if (cooldownRemaining.value === null) return
          if (cooldownRemaining.value <= 1) {
            cooldownRemaining.value = null
            clearCooldownTimer()
            serverError.value = null
          } else {
            cooldownRemaining.value -= 1
            serverError.value = `쿨다운 ${cooldownRemaining.value}초 남았습니다.`
          }
        }, 1000)
        showToast({ message: `쿨다운 ${cooldownRemaining.value}초 남았습니다.`, kind: 'warn' })
      } else {
        serverError.value = err.message
      }
      statusText.value = '서버 응답으로 시작하지 못했습니다.'
      return
    }
    serverError.value = '서버와 통신할 수 없습니다. 오프라인 시뮬레이션으로 진행합니다.'
    rng.value = createRng(clientSeed)
    raceMeta.value = { id: null, seed: clientSeed, fairnessHash: null }
    resetPositions()
    started = true
  } finally {
    isStarting.value = false
  }

  if (!started) return

  raceState.value = 'running'
  statusText.value = '경주 중...'
  frameCount.value = 0
  raceStartedAt.value = performance.now()
  tick()
}

const reportResultToServer = async () => {
  if (!raceMeta.value.id || !raceMeta.value.seed) return
  isReporting.value = true
  try {
    const durationMs = raceStartedAt.value ? Math.round(performance.now() - raceStartedAt.value) : 0
    const result = await completeSnailRace(raceMeta.value.id, {
      raceId: raceMeta.value.id,
      winnerIds: winnerIds.value,
      durationMs,
      frames: frameCount.value,
      seed: raceMeta.value.seed,
      betAmount: betAmount.value,
      betSnailId: selectedSnailId.value,
      clientLog: { positions: snails.value.map((s) => Math.round(s.position)) },
    })

    serverResult.value = result
    raceMeta.value.fairnessHash = result.fairnessHash ?? raceMeta.value.fairnessHash
    statusText.value = result.verified
      ? '서버 검증 완료! 결과가 저장되었습니다.'
      : '결과 저장은 완료, 추가 검증이 필요합니다.'
  } catch (err) {
    if (err instanceof GameApiError) {
      serverError.value = err.message
    } else {
      serverError.value = '결과 저장에 실패했습니다. 네트워크 상태를 확인해 주세요.'
    }
  } finally {
    isReporting.value = false
  }
}

const finishRace = () => {
  if (raceState.value !== 'running') return
  raceState.value = 'finished'
  const maxPos = Math.max(...snails.value.map((s) => s.position))
  winnerIds.value = snails.value.filter((s) => s.position >= maxPos - 1).map((s) => s.id)
  const userWon = winnerIds.value.includes(selectedSnailId.value)
  statusText.value = userWon ? '축하! 내가 고른 달팽이가 1등' : '아쉽지만 다음 기회에'
  if (raceMeta.value.id) {
    void reportResultToServer()
  }
}

const retryReport = () => {
  if (isReporting.value || !raceMeta.value.id) return
  void reportResultToServer()
}

const copyMeta = async (text: string | null, label: string) => {
  if (!text || !navigator?.clipboard) return
  try {
    await navigator.clipboard.writeText(text)
    copyStatus.value = `${label} 복사 완료`
  } catch (err) {
    copyStatus.value = (err as Error).message ?? '클립보드 복사에 실패했습니다.'
  } finally {
    window.setTimeout(() => {
      copyStatus.value = null
    }, 1800)
  }
}

const tick = () => {
  const canvas = canvasRef.value
  if (!canvas) return

  const random = rng.value
  snails.value = snails.value.map((s) => {
    const burst = random() * 0.6
    const drift = random() * 0.25
    const delta = s.baseSpeed + burst - drift * 0.3
    const next = Math.min(trackLength.value, s.position + delta)
    return { ...s, position: next }
  })

  frameCount.value += 1
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
  clearCooldownTimer()
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

.bet-row {
  display: grid;
  gap: 0.35rem;
}

.bet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.balance {
  font-weight: 800;
  color: #0f172a;
  background: #f3f4f6;
  border-radius: 10px;
  padding: 0.3rem 0.55rem;
  font-size: 0.9rem;
}

.bet-input {
  display: grid;
  gap: 0.35rem;
}

.bet-input input {
  width: 100%;
  padding: 0.65rem 0.75rem;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  font-weight: 700;
}

.bet-hint {
  display: flex;
  gap: 0.75rem;
  color: #6b7280;
  font-size: 0.85rem;
}

.cooldown-chip {
  padding: 0.1rem 0.55rem;
  border-radius: 999px;
  border: 1px solid #f59e0b;
  color: #92400e;
  background: #fef3c7;
  font-weight: 700;
}

.error {
  margin: 0;
  color: #dc2626;
  font-weight: 700;
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
  transition:
    transform 0.12s ease,
    box-shadow 0.12s ease,
    border-color 0.12s ease;
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
  transition:
    transform 0.12s ease,
    box-shadow 0.12s ease,
    background 0.12s ease;
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

.server-error {
  margin: 0;
  color: #b91c1c;
  background: #fef2f2;
  border: 1px solid #fecdd3;
  border-radius: 10px;
  padding: 0.65rem 0.75rem;
  font-weight: 700;
}

.canvas-wrap {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 0.75rem;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
  margin-bottom: 1rem;
}

.meta-bar {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}

.meta p {
  margin: 0;
  color: #6b7280;
  font-weight: 700;
  font-size: 0.9rem;
}

.meta strong {
  display: block;
  color: #111827;
  margin-top: 0.15rem;
}

.meta .mono {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 0.95rem;
  word-break: break-all;
}

.meta .verified {
  color: #059669;
}

.report-row {
  margin-top: 0.65rem;
  display: flex;
  justify-content: flex-end;
}

.copy-status {
  margin-top: 0.35rem;
  color: #2563eb;
  font-weight: 700;
}

.fairness-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.ghost.tiny {
  padding: 0.35rem 0.55rem;
  font-size: 0.8rem;
}

.race-canvas {
  width: 100%;
  display: block;
}

.fairness {
  margin-top: 0.5rem;
  background: #f9fafb;
  border: 1px dashed #e5e7eb;
  border-radius: 10px;
  padding: 0.65rem 0.75rem;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 0.9rem;
  color: #111827;
  word-break: break-all;
}

.server-note {
  margin-top: 0.4rem;
  color: #4b5563;
  font-size: 0.9rem;
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
