import type { TokenResponse } from '@/types/auth'

const BASE_URL = '/api'

type FetchOptions = RequestInit & {
  headers?: Record<string, string>
}

let accessToken: string | null = null
let isRefreshing = false
let refreshSubscribers: ((token: string) => void)[] = []

export const setAccessToken = (token: string | null) => {
  accessToken = token
}

export const getAccessToken = () => accessToken

const onRefreshed = (token: string) => {
  refreshSubscribers.forEach((callback) => callback(token))
  refreshSubscribers = []
}

const addRefreshSubscriber = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback)
}

export const fetchClient = async (url: string, options: FetchOptions = {}): Promise<Response> => {
  const fullUrl = url.startsWith('/') ? url : `${BASE_URL}${url}`

  const headers: Record<string, string> = { ...options.headers }
  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`
  }

  let response = await fetch(fullUrl, {
    ...options,
    headers,
  })

  if (response.status === 401) {
    if (!isRefreshing) {
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
        } else {
          setAccessToken(null)
          // 갱신 실패 시 대기 중인 요청들도 실패 처리하거나 로그아웃 처리
          refreshSubscribers = [] 
        }
      } catch (e) {
        setAccessToken(null)
        refreshSubscribers = []
      } finally {
        isRefreshing = false
      }
    }

    // 토큰 갱신 중이거나 방금 갱신을 시도했다면, 갱신 완료를 기다린 후 재요청
    if (isRefreshing) {
      return new Promise<Response>((resolve) => {
        addRefreshSubscriber((newToken) => {
          headers['Authorization'] = `Bearer ${newToken}`
          resolve(fetch(fullUrl, { ...options, headers }))
        })
      })
    }

    // 이미 갱신된 상태라면(동시성 이슈로 인해) 바로 재요청
    if (accessToken) {
       headers['Authorization'] = `Bearer ${accessToken}`
       return fetch(fullUrl, { ...options, headers })
    }
    
    return response
  }

  return response
}
