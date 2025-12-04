import type { TokenResponse } from '@/types/auth'
import { ref } from 'vue'

const BASE_URL = '/api'

type FetchOptions = RequestInit & {
  headers?: Record<string, string>
}

const accessToken = ref<string | null>(null)
let isRefreshing = false
let refreshSubscribers: ((token: string | null) => void)[] = []

export const setAccessToken = (token: string | null) => {
  accessToken.value = token
}

export const getAccessToken = () => accessToken.value

const onRefreshed = (token: string | null) => {
  refreshSubscribers.forEach((callback) => callback(token))
  refreshSubscribers = []
}

const addRefreshSubscriber = (callback: (token: string | null) => void) => {
  refreshSubscribers.push(callback)
}

export const isTokenExpiringSoon = (token: string, thresholdSeconds = 300): boolean => {
  try {
    const parts = token.split('.')
    const payloadPart = parts[1]
    if (!payloadPart) return true
    const payload = JSON.parse(atob(payloadPart.replace(/-/g, '+').replace(/_/g, '/')))
    if (!payload.exp) return false
    const now = Math.floor(Date.now() / 1000)
    return payload.exp - now < thresholdSeconds
  } catch (e) {
    return true // 파싱 실패 시 만료된 것으로 간주
  }
}

export const refreshAccessToken = async (): Promise<string | null> => {
  if (isRefreshing) {
    return new Promise((resolve) => {
      addRefreshSubscriber(resolve)
    })
  }

  isRefreshing = true
  try {
    const refreshResponse = await fetch(`${BASE_URL}/auth/refresh`, {
      method: 'POST',
      credentials: 'include',
    })

    if (refreshResponse.ok) {
      const data = (await refreshResponse.json()) as { token: TokenResponse }
      const newToken = data.token.accessToken
      setAccessToken(newToken)
      onRefreshed(newToken)
      return newToken
    } else {
      setAccessToken(null)
      onRefreshed(null)
      return null
    }
  } catch (e) {
    setAccessToken(null)
    onRefreshed(null)
    return null
  } finally {
    isRefreshing = false
  }
}

export const fetchClient = async (url: string, options: FetchOptions = {}): Promise<Response> => {
  const fullUrl = url.startsWith('/') ? url : `${BASE_URL}${url}`

  // 1. 토큰 만료 임박 확인 (5분 미만)
  if (accessToken.value && isTokenExpiringSoon(accessToken.value)) {
    console.log('[API] Token expiring soon, refreshing proactively...')
    await refreshAccessToken()
  }

  const headers: Record<string, string> = { ...options.headers }
  if (accessToken.value) {
    headers['Authorization'] = `Bearer ${accessToken.value}`
  }

  const response = await fetch(fullUrl, {
    ...options,
    headers,
  })

  if (response.status === 401) {
    // 2. 401 발생 시 토큰 갱신 시도 (이미 위에서 갱신했더라도, 시점 차이나 다른 이유로 401이 날 수 있음)
    const newToken = await refreshAccessToken()

    if (newToken) {
      headers['Authorization'] = `Bearer ${newToken}`
      return fetch(fullUrl, { ...options, headers })
    }
  }

  return response
}
