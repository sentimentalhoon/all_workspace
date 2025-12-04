import type {
  ProfileResponse,
  TelegramAuthResponse,
  TelegramLoginPayload,
  UserResponse,
} from '@/types/auth'
import { fetchClient, getAccessToken, refreshAccessToken, setAccessToken } from '@/utils/api'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserResponse | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const isAuthenticated = computed(() => !!user.value)

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
      setAccessToken(data.token.accessToken)
      localStorage.setItem('wasLoggedIn', 'true')

      return data
    } catch (err) {
      handleError(err, '로그인 중 알 수 없는 오류가 발생했습니다.')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchProfile() {
    loading.value = true
    error.value = null

    try {
      // fetchClient 내부에서 401 시 자동 갱신 시도함
      const response = await fetchClient('/api/me')

      if (response.status === 401) {
        logout()
        throw new Error('세션이 만료되었습니다. 다시 로그인해 주세요.')
      }

      const data = (await response.json()) as ProfileResponse
      if (!response.ok) {
        throw new Error(
          (data as { message?: string }).message ?? '프로필 정보를 불러오지 못했습니다.',
        )
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

  async function checkAuth() {
    // 이전에 로그인한 적이 없다면 불필요한 요청을 보내지 않음 (401 에러 방지)
    if (!localStorage.getItem('wasLoggedIn')) {
      return
    }

    // 앱 시작 시 AccessToken이 없으면 먼저 갱신 시도 (401 에러 방지)
    if (!getAccessToken()) {
      try {
        await refreshAccessToken()
      } catch (e) {
        // 갱신 실패 시 아래 fetchProfile에서 처리하거나 여기서 종료
      }
    }

    // 앱 시작 시 호출: AccessToken 이 없어도 /api/me 를 호출하면
    // fetchClient 가 401 -> refresh -> retry 과정을 거쳐 로그인을 복구함
    try {
      await fetchProfile()
    } catch {
      // 실패하면 로그아웃 상태 유지
      user.value = null
      localStorage.removeItem('wasLoggedIn')
    }
  }

  async function logout() {
    try {
      await fetch('/api/auth/logout', { method: 'POST' })
    } catch (e) {
      // ignore
    }
    user.value = null
    setAccessToken(null)
    error.value = null
    localStorage.removeItem('wasLoggedIn')
  }

  return {
    user,
    loading,
    error,
    isAuthenticated,
    loginWithTelegramPayload,
    fetchProfile,
    checkAuth,
    logout,
  }
})
