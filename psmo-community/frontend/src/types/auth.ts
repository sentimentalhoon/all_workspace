export interface TokenResponse {
  accessToken: string
  expiresAt: number
}

export interface UserResponse {
  id: number
  telegramId: number
  displayName?: string | null
  username?: string | null
  photoUrl?: string | null
}

export interface ProfileResponse {
  status: string
  user: UserResponse
}

export interface TelegramAuthResponse {
  status: string
  user: UserResponse
  token: TokenResponse
  authDate: number
  verifiedAt: number
}

export interface TelegramLoginPayload {
  id: number
  first_name: string
  last_name?: string
  username?: string
  photo_url?: string
  auth_date: number
  hash: string
}
