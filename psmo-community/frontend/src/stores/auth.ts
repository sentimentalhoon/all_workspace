import { defineStore } from 'pinia'
import { computed, ref, watch } from 'vue'
import type {
  ProfileResponse,
  TelegramAuthResponse,
  TelegramLoginPayload,
  TokenResponse,
  UserResponse,
} from '@/types/auth'

const STORAGE_KEY = 'psmo-auth-session'

type StoredSession = {
  user: UserResponse
  token: TokenResponse
}

const loadStoredSession = (): StoredSession | null => {
  if (typeof window === 'undefined') return null
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as StoredSession) : null
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const persisted = loadStoredSession()
  const user = ref<UserResponse | null>(persisted?.user ?? null)
  const token = ref<TokenResponse | null>(persisted?.token ?? null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => Boolean(token.value?.accessToken))

  const persistSession = () => {
    if (typeof window === 'undefined') return
    if (user.value && token.value) {
      const payload: StoredSession = {
        user: user.value,
        token: token.value,
      }
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(payload))
    } else {
      window.localStorage.removeItem(STORAGE_KEY)
    }
  }

  watch([user, token], persistSession, { deep: true })

  const handleError = (err: unknown, fallback: string) => {
    const message = err instanceof Error ? err.message : fallback
    error.value = message
    return message
  }

  async function loginWithTelegramPayload(payload: TelegramLoginPayload) {
    loading.value = true
    error.value = null

    try {
      const params = new URLSearchParams()
      Object.entries(payload).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          params.append(key, String(value))
        }
      })

      const response = await fetch('/api/auth/telegram', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params,
      })

      const data = (await response.json()) as TelegramAuthResponse
      if (!response.ok) {
        throw new Error((data as { message?: string }).message ?? '텔레그램 인증에 실패했습니다.')
      }

      user.value = data.user
      token.value = data.token

      return data
    } catch (err) {
      handleError(err, '로그인 중 알 수 없는 오류가 발생했습니다.')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchProfile() {
    if (!token.value) return null

    loading.value = true
    error.value = null

    try {
      const headers: HeadersInit = token.value
        ? { Authorization: `Bearer ${token.value.accessToken}` }
        : {}

      const response = await fetch('/api/me', { headers })

      if (response.status === 401) {
        logout()
        throw new Error('세션이 만료되었습니다. 다시 로그인해 주세요.')
      }

      const data = (await response.json()) as ProfileResponse
      if (!response.ok) {
        throw new Error((data as { message?: string }).message ?? '프로필 정보를 불러오지 못했습니다.')
      }

      user.value = data.user
      return data
    } catch (err) {
      handleError(err, '프로필 조회 중 오류가 발생했습니다.')
      throw err
    } finally {
      loading.value = false
    }
  }

  function logout() {
    user.value = null
    token.value = null
    error.value = null
    persistSession()
  }

  return {
    user,
    token,
    loading,
    error,
    isAuthenticated,
    loginWithTelegramPayload,
    fetchProfile,
    logout,
  }
})
