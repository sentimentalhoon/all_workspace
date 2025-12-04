import type { TokenResponse } from '@/types/auth'

const BASE_URL = '/api'

type FetchOptions = RequestInit & {
  headers?: Record<string, string>
}

let accessToken: string | null = null

export const setAccessToken = (token: string | null) => {
  accessToken = token
}

export const getAccessToken = () => accessToken

export const fetchClient = async (url: string, options: FetchOptions = {}): Promise<Response> => {
  const fullUrl = url.startsWith('/') ? url : `${BASE_URL}${url}`

  const headers = { ...options.headers }
  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`
  }

  let response = await fetch(fullUrl, {
    ...options,
    headers,
  })

  if (response.status === 401) {
    // 401 발생 시 토큰 갱신 시도
    try {
      const refreshResponse = await fetch(`${BASE_URL}/auth/refresh`, {
        method: 'POST',
      })

      if (refreshResponse.ok) {
        const data = (await refreshResponse.json()) as { token: TokenResponse }
        setAccessToken(data.token.accessToken)

        // 새 토큰으로 헤더 업데이트 후 재요청
        if (accessToken) {
          headers['Authorization'] = `Bearer ${accessToken}`
        }
        response = await fetch(fullUrl, {
          ...options,
          headers,
        })
      } else {
        // 갱신 실패 시 로그아웃 처리 (호출부에서 처리하도록 에러 전파)
        setAccessToken(null)
      }
    } catch (e) {
      setAccessToken(null)
    }
  }

  return response
}
