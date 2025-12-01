<template>
  <div class="my-view">
    <div class="page-header">
      <h2>내 정보</h2>
    </div>

    <div class="my-container">
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
              <button class="btn-outline" @click="refreshProfile" :disabled="loading">
                {{ loading ? '새로고침 중...' : '프로필 새로고침' }}
              </button>
              <button class="btn-secondary" @click="handleLogout">로그아웃</button>
            </div>
          </template>

          <template v-else>
            <p class="login-prompt">
              텔레그램으로 로그인하면 맞춤형 게시판, 채팅 기록, 알림 설정을 이용할 수 있어요.
            </p>
            <button class="btn-login" @click="handleTelegramLogin" :disabled="loginPending || loading">
              {{ loginPending || loading ? '로그인 중...' : '텔레그램으로 로그인' }}
            </button>
            <p class="login-helper">Bot: <span>{{ telegramBotUsername }}</span></p>
          </template>

          <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        </div>
      </div>

      <div class="stats-section">
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">작성 글</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">댓글</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">저장한 글</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">팔로워</div>
        </div>
      </div>

      <div class="menu-section">
        <h3>활동</h3>
        <div class="menu-items">
          <button class="menu-item">
            <span class="menu-icon">🗂️</span>
            <span class="menu-label">내가 쓴 글</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">💬</span>
            <span class="menu-label">댓글 단 글</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">⭐</span>
            <span class="menu-label">즐겨찾기</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">📩</span>
            <span class="menu-label">메시지함</span>
            <span class="menu-arrow">›</span>
          </button>
        </div>
      </div>

      <div class="menu-section">
        <h3>알림 & 정보</h3>
        <div class="menu-items">
          <button class="menu-item">
            <span class="menu-icon">🔔</span>
            <span class="menu-label">알림 설정</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">📣</span>
            <span class="menu-label">공지사항</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">📝</span>
            <span class="menu-label">피드백</span>
            <span class="menu-arrow">›</span>
          </button>
        </div>
      </div>

      <div class="menu-section">
        <h3>설정</h3>
        <div class="menu-items">
          <button class="menu-item">
            <span class="menu-icon">⚙️</span>
            <span class="menu-label">환경설정</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">🛡️</span>
            <span class="menu-label">보안 설정</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">💳</span>
            <span class="menu-label">멤버십</span>
            <span class="menu-arrow">›</span>
          </button>
        </div>
      </div>

      <div class="menu-section">
        <h3>지원</h3>
        <div class="menu-items">
          <button class="menu-item">
            <span class="menu-icon">🙋</span>
            <span class="menu-label">도움말</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">❓</span>
            <span class="menu-label">FAQ</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">📧</span>
            <span class="menu-label">문의하기</span>
            <span class="menu-arrow">›</span>
          </button>
        </div>
      </div>

      <div class="footer-links">
        <a href="#">이용약관</a>
        <span class="divider">|</span>
        <a href="#">개인정보처리방침</a>
        <span class="divider">|</span>
        <RouterLink to="/admin">관리자</RouterLink>
      </div>

      <div class="app-info">
        <p>PSMO Community v1.0.0</p>
        <p>ⓒ 2024 PSMO. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const { user, isAuthenticated, loading, error } = storeToRefs(authStore)

const loginPending = ref(false)
const telegramError = ref<string | null>(null)
const telegramReady = ref(false)

const telegramBotId = Number(import.meta.env.VITE_TELEGRAM_BOT_ID ?? 0)
const telegramBotUsername = import.meta.env.VITE_TELEGRAM_BOT_USERNAME ?? 'Psmo_community_bot'

const avatarUrl = computed(
  () => user.value?.photoUrl || 'https://via.placeholder.com/80?text=PSMO',
)

const errorMessage = computed(() => telegramError.value ?? error.value)

const ensureTelegramScript = () => {
  if (telegramReady.value) return Promise.resolve()
  if (typeof window === 'undefined') {
    return Promise.reject(new Error('브라우저 환경에서만 로그인할 수 있습니다.'))
  }

  return new Promise<void>((resolve, reject) => {
    if (window.Telegram?.Login) {
      telegramReady.value = true
      resolve()
      return
    }

    let script = document.getElementById('telegram-login-sdk') as HTMLScriptElement | null
    if (script) {
      script.addEventListener(
        'load',
        () => {
          telegramReady.value = true
          resolve()
        },
        { once: true },
      )
      script.addEventListener(
        'error',
        () => reject(new Error('텔레그램 스크립트를 불러오지 못했습니다.')),
        { once: true },
      )
      return
    }

    script = document.createElement('script')
    script.id = 'telegram-login-sdk'
    script.src = 'https://telegram.org/js/telegram-widget.js?22'
    script.async = true
    script.onload = () => {
      telegramReady.value = true
      resolve()
    }
    script.onerror = () => reject(new Error('텔레그램 스크립트를 불러오지 못했습니다.'))
    document.body.appendChild(script)
  })
}

const requestTelegramAuth = () =>
  new Promise<void>((resolve, reject) => {
    const login = window.Telegram?.Login
    if (!login?.auth) {
      reject(new Error('텔레그램 로그인 위젯을 초기화하지 못했습니다.'))
      return
    }

    login.auth(
      {
        bot_id: telegramBotId,
        request_access: 'write',
      },
      async (payload) => {
        if (!payload) {
          reject(new Error('로그인이 취소되었습니다.'))
          return
        }
        try {
          await authStore.loginWithTelegramPayload(payload)
          resolve()
        } catch (err) {
          reject(err)
        }
      },
    )
  })

const handleTelegramLogin = async () => {
  telegramError.value = null

  if (!telegramBotId) {
    telegramError.value = 'VITE_TELEGRAM_BOT_ID 환경 변수를 설정해 주세요.'
    return
  }

  loginPending.value = true
  try {
    await ensureTelegramScript()
    await requestTelegramAuth()
    await authStore.fetchProfile()
  } catch (err) {
    telegramError.value =
      err instanceof Error ? err.message : '텔레그램 로그인 중 오류가 발생했습니다.'
  } finally {
    loginPending.value = false
  }
}

const handleLogout = () => {
  authStore.logout()
  telegramError.value = null
}

const refreshProfile = async () => {
  telegramError.value = null
  try {
    await authStore.fetchProfile()
  } catch (err) {
    telegramError.value =
      err instanceof Error ? err.message : '프로필을 새로고침 할 수 없습니다.'
  }
}

onMounted(() => {
  ensureTelegramScript().catch(() => undefined)

  if (isAuthenticated.value && !user.value) {
    authStore.fetchProfile().catch(() => undefined)
  }
})
</script>

<style scoped>
.my-view {
  background: #f8f9fa;
  min-height: calc(100vh - 120px);
}

.page-header {
  background: white;
  padding: 1.5rem 1rem;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.page-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #2d3748;
}

.my-container {
  padding: 1rem;
}

.profile-section {
  background: white;
  padding: 2rem 1rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.profile-avatar {
  display: flex;
  justify-content: center;
  margin-bottom: 1rem;
}

.profile-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid #667eea;
  object-fit: cover;
}

.profile-info .login-prompt {
  margin: 0 0 1rem 0;
  color: #666;
  font-size: 0.95rem;
}

.profile-name {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 700;
  color: #1a202c;
}

.profile-username {
  margin: 0.25rem 0 0;
  color: #718096;
  font-size: 0.95rem;
}

.profile-meta {
  margin: 0.5rem 0 1rem;
  color: #4a5568;
  font-size: 0.9rem;
}

.profile-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-login,
.btn-outline,
.btn-secondary {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: background 0.2s, color 0.2s;
}

.btn-login {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  width: 100%;
  max-width: 280px;
  margin: 0 auto;
}

.btn-login:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-outline {
  background: transparent;
  border: 1px solid #667eea;
  color: #667eea;
}

.btn-outline:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #edf2f7;
  color: #4a5568;
}

.login-helper {
  margin-top: 0.75rem;
  font-size: 0.85rem;
  color: #718096;
}

.login-helper span {
  font-weight: 600;
  color: #4c51bf;
}

.error-message {
  margin-top: 0.75rem;
  color: #e53e3e;
  font-size: 0.9rem;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
  background: white;
  padding: 1rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.75rem;
  color: #666;
}

.menu-section {
  background: white;
  padding: 1rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.menu-section h3 {
  margin: 0 0 0.75rem 0;
  font-size: 0.9rem;
  color: #666;
  font-weight: 600;
  padding-left: 0.5rem;
}

.menu-items {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 1rem 0.5rem;
  background: none;
  border: none;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
  text-align: left;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-icon {
  font-size: 1.25rem;
  margin-right: 0.75rem;
}

.menu-label {
  flex: 1;
  font-size: 0.95rem;
  color: #2d3748;
}

.menu-arrow {
  font-size: 1.25rem;
  color: #ccc;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin: 2rem 0 1rem 0;
  font-size: 0.85rem;
}

.footer-links a {
  color: #666;
  text-decoration: none;
}

.footer-links .divider {
  color: #ccc;
}

.app-info {
  text-align: center;
  color: #999;
  font-size: 0.8rem;
  padding-bottom: 1rem;
}

.app-info p {
  margin: 0.25rem 0;
}
</style>
