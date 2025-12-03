import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref } from 'vue'

const TELEGRAM_WIDGET_SRC = 'https://telegram.org/js/telegram-widget.js?22'
const TELEGRAM_SCRIPT_ID = 'telegram-login-sdk'

const ensureWindow = () => {
  if (typeof window === 'undefined') {
    throw new Error('이 기능은 브라우저 환경에서만 사용할 수 있습니다.')
  }
}

const loadTelegramScript = (readyFlag: { value: boolean }) => {
  ensureWindow()

  if (readyFlag.value) {
    return Promise.resolve()
  }

  return new Promise<void>((resolve, reject) => {
    if (window.Telegram?.Login) {
      readyFlag.value = true
      resolve()
      return
    }

    let script = document.getElementById(TELEGRAM_SCRIPT_ID) as HTMLScriptElement | null
    if (script) {
      script.addEventListener(
        'load',
        () => {
          readyFlag.value = true
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
    script.id = TELEGRAM_SCRIPT_ID
    script.src = TELEGRAM_WIDGET_SRC
    script.async = true
    script.onload = () => {
      readyFlag.value = true
      resolve()
    }
    script.onerror = () => reject(new Error('텔레그램 스크립트를 불러오지 못했습니다.'))
    document.body.appendChild(script)
  })
}

const requestTelegramAuth = (botId: number, authStore: ReturnType<typeof useAuthStore>) => {
  ensureWindow()

  return new Promise<void>((resolve, reject) => {
    const login = window.Telegram?.Login
    if (!login?.auth) {
      reject(new Error('텔레그램 로그인 위젯을 초기화하지 못했습니다.'))
      return
    }

    login.auth(
      {
        bot_id: botId,
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
}

export function useTelegramAuth() {
  const authStore = useAuthStore()
  const { user, isAuthenticated, loading, error } = storeToRefs(authStore)

  const loginPending = ref(false)
  const telegramError = ref<string | null>(null)
  const telegramReady = ref(false)

  const telegramBotId = Number(import.meta.env.VITE_TELEGRAM_BOT_ID ?? 0)
  const telegramBotUsername = import.meta.env.VITE_TELEGRAM_BOT_USERNAME ?? 'Psmo_community_bot'

  const errorMessage = computed(() => telegramError.value ?? error.value ?? null)

  const handleTelegramLogin = async () => {
    telegramError.value = null

    if (!telegramBotId) {
      telegramError.value = 'VITE_TELEGRAM_BOT_ID 환경 변수를 설정해 주세요.'
      return
    }

    loginPending.value = true
    try {
      await loadTelegramScript(telegramReady)
      await requestTelegramAuth(telegramBotId, authStore)
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
      telegramError.value = err instanceof Error ? err.message : '프로필을 새로고침 할 수 없습니다.'
      throw err
    }
  }

  onMounted(() => {
    loadTelegramScript(telegramReady).catch(() => undefined)

    if (isAuthenticated.value && !user.value) {
      authStore.fetchProfile().catch(() => undefined)
    }
  })

  return {
    user,
    isAuthenticated,
    loading,
    errorMessage,
    loginPending,
    telegramBotUsername,
    handleTelegramLogin,
    handleLogout,
    refreshProfile,
  }
}
