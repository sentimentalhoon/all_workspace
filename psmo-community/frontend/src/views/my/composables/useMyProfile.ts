import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref } from 'vue'

const TELEGRAM_WIDGET_SRC = 'https://telegram.org/js/telegram-widget.js?22'

export function useMyProfile() {
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
      script.src = TELEGRAM_WIDGET_SRC
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
      telegramError.value = err instanceof Error ? err.message : '프로필을 새로고침 할 수 없습니다.'
    }
  }

  onMounted(() => {
    ensureTelegramScript().catch(() => undefined)

    if (isAuthenticated.value && !user.value) {
      authStore.fetchProfile().catch(() => undefined)
    }
  })

  return {
    user,
    isAuthenticated,
    loading,
    avatarUrl,
    errorMessage,
    loginPending,
    telegramBotUsername,
    handleTelegramLogin,
    handleLogout,
    refreshProfile,
  }
}
