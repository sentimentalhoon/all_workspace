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
import type { UserResponse } from '@/types/auth'

withDefaults(
  defineProps<{
    isAuthenticated: boolean
    user: UserResponse | null
    avatarUrl: string
    loading: boolean
    loginPending: boolean
    errorMessage: string | null
    telegramBotUsername: string
  }>(),
  {
    user: null,
    errorMessage: null,
  },
)

const emit = defineEmits<{
  (e: 'login'): void
  (e: 'refresh'): void
  (e: 'logout'): void
}>()
</script>
