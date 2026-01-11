<template>
  <main class="login">
    <section class="card">
      <header>
        <p class="eyebrow">Private Access</p>
        <h1>텔레그램으로 로그인</h1>
        <p class="muted">
          PSMO는 텔레그램 인증을 거친 사용자만 이용할 수 있습니다. 아래 버튼을 눌러 로그인해 주세요.
        </p>
      </header>

      <div class="tabs">
        <button :class="{ active: loginMethod === 'widget' }" @click="loginMethod = 'widget'">
          기본 로그인
        </button>
        <button :class="{ active: loginMethod === 'qr' }" @click="loginMethod = 'qr'">
          QR 코드
        </button>
      </div>

      <div v-if="loginMethod === 'widget'" class="actions">
        <button class="primary" :disabled="loginPending" @click="handleLogin">
          <span v-if="loginPending">로그인 중...</span>
          <span v-else>텔레그램 앱으로 계속하기 (@{{ telegramBotUsername }})</span>
        </button>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      </div>

      <div v-else class="qr-section">
        <TelegramQrLogin />
      </div>

      <p class="hint">로그인 완료 시 요청하신 페이지로 이동합니다.</p>
    </section>
  </main>
</template>

<script setup lang="ts">
import TelegramQrLogin from '@/components/auth/TelegramQrLogin.vue' // Import QR Component
import { useTelegramAuth } from '@/composables/useTelegramAuth'
import { computed, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const redirectTo = computed(() => (route.query.redirect as string | undefined) ?? '/')

const loginMethod = ref<'widget' | 'qr'>('qr') // Default to QR for better UX? Or remember last choice? Let's default to QR as it's the new feature.

const { isAuthenticated, loginPending, errorMessage, handleTelegramLogin, telegramBotUsername } =
  useTelegramAuth()

const navigateIfReady = () => {
  if (isAuthenticated.value) {
    router.replace(redirectTo.value)
  }
}

watchEffect(navigateIfReady)

const handleLogin = async () => {
  try {
    await handleTelegramLogin()
    await router.replace(redirectTo.value)
  } catch {
    // error handled inside composable
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: radial-gradient(circle at 20% 20%, #1b2735, #090a0f 45%);
  padding: 32px;
  color: #f5f7fb;
}

.card {
  background: rgba(18, 24, 38, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 28px;
  max-width: 460px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(10px);
}

header {
  display: grid;
  gap: 8px;
  margin-bottom: 20px;
}

.eyebrow {
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #6bc7ff;
  font-weight: 700;
  font-size: 12px;
}

h1 {
  font-size: 26px;
  margin: 0;
  color: #e9ecf5;
}

.muted {
  color: #b7c3d9;
  line-height: 1.5;
  margin: 0;
}

/* Tabs */
.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.05);
  padding: 4px;
  border-radius: 10px;
}

.tabs button {
  flex: 1;
  background: transparent;
  border: none;
  color: #9fb1cc;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}

.tabs button.active {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.actions {
  display: grid;
  gap: 12px;
}

.qr-section {
  display: flex;
  justify-content: center;
}

.primary {
  width: 100%;
  border: none;
  border-radius: 12px;
  padding: 14px 16px;
  background: linear-gradient(135deg, #4bb0ff, #5ce1ec);
  color: #0a0b10;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease;
}

.primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.primary:not(:disabled):hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(91, 207, 255, 0.3);
}

.error {
  background: rgba(255, 91, 91, 0.12);
  border: 1px solid rgba(255, 91, 91, 0.3);
  color: #ffc2c2;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 14px;
}

.hint {
  color: #9fb1cc;
  font-size: 13px;
  margin-top: 16px;
  text-align: center;
}
</style>
