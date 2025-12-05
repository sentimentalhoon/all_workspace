<template>
  <div class="profile-section">
    <div class="profile-avatar" :class="{ 'with-image': isAuthenticated }">
      <img :src="avatarUrl" alt="프로필 이미지" />
    </div>

    <div class="profile-info">
      <template v-if="isAuthenticated">
        <p class="profile-name">{{ user?.displayName ?? 'PSMO 사용자' }}</p>
        <p v-if="user?.username" class="profile-username">@{{ user.username }}</p>
        <p class="profile-meta">텔레그램 계정과 연동되었습니다.</p>

        <div class="badges">
          <span class="badge role">{{ roleLabel }}</span>
          <span class="badge level">Lv. {{ activityLevel }}</span>
        </div>

        <div class="progress">
          <div class="label">
            <span>점수 {{ score }}</span>
            <span v-if="nextLevelInfo.nextLevel">다음 Lv.{{ nextLevelInfo.nextLevel }}까지 {{ nextLevelInfo.remaining }}점</span>
            <span v-else>최고 레벨</span>
          </div>
          <div class="bar">
            <div class="fill" :style="{ width: progressWidth }"></div>
          </div>
        </div>

        <div class="profile-actions">
          <button class="btn-outline" @click="emit('refresh')" :disabled="loading">
            {{ loading ? '새로고침 중...' : '프로필 새로고침' }}
          </button>
          <button class="btn-secondary" @click="emit('logout')">로그아웃</button>
        </div>
      </template>

      <template v-else>
        <p class="login-prompt">
          텔레그램으로 로그인하면 맞춤형 게시판, 채팅 기록, 알림 설정을 이용할 수 있어요.
        </p>
        <button class="btn-login" @click="emit('login')" :disabled="loginPending || loading">
          {{ loginPending || loading ? '로그인 중...' : '텔레그램으로 로그인' }}
        </button>
        <p class="login-helper">
          Bot: <span>{{ telegramBotUsername }}</span>
        </p>
      </template>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { UserResponse } from '@/types/auth'

const props = withDefaults(
  defineProps<{
    isAuthenticated: boolean
    user: UserResponse | null
    avatarUrl: string
    loading: boolean
    loginPending: boolean
    errorMessage: string | null
    telegramBotUsername: string
    role: string
    score: number
    activityLevel: number
    nextLevelInfo: {
      nextLevel: number | null
      remaining: number
    }
  }>(),
  {
    user: null,
    errorMessage: null,
  },
)

const roleLabel = computed(() => props.role?.toUpperCase?.() ?? 'MEMBER')
const progressWidth = computed(() => {
  if (!props.nextLevelInfo.nextLevel) return '100%'
  const currentMin = getMinForLevel(props.activityLevel)
  const nextMin = getMinForLevel(props.nextLevelInfo.nextLevel)
  const span = Math.max(1, nextMin - currentMin)
  const gained = Math.min(span, Math.max(0, props.score - currentMin))
  const ratio = gained / span
  return `${Math.round(ratio * 100)}%`
})

function getMinForLevel(level: number): number {
  switch (level) {
    case 1:
      return 0
    case 2:
      return 100
    case 3:
      return 300
    case 4:
      return 700
    case 5:
      return 1500
    default:
      return 0
  }
}

const emit = defineEmits<{
  (e: 'login'): void
  (e: 'refresh'): void
  (e: 'logout'): void
}>()
</script>

<style scoped>
.badges {
  display: flex;
  gap: 8px;
  margin: 6px 0 10px;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.badge.role {
  background: #eef2ff;
  color: #4338ca;
}

.badge.level {
  background: #ecfdf3;
  color: #166534;
}

.progress {
  display: grid;
  gap: 6px;
  margin-bottom: 12px;
}

.progress .label {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #4b5563;
}

.progress .bar {
  background: #eef2ff;
  border-radius: 999px;
  height: 8px;
  overflow: hidden;
}

.progress .fill {
  background: linear-gradient(135deg, #4bb0ff, #5ce1ec);
  height: 100%;
  transition: width 0.2s ease;
}
</style>
