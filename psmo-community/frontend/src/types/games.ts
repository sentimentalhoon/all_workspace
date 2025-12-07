export type SnailRunner = {
  id: number
  name: string
  color: string
  baseSpeed: number
}

export type SnailRaceLimits = {
  minBet: number
  maxBet: number
  step: number
  cooldownSeconds?: number
}

export type SnailRaceStartRequest = {
  snailId: number
  betAmount: number
  clientSeed?: string
}

export type SnailRaceStartResponse = {
  raceId: string
  seed: string
  snails: SnailRunner[]
  trackLength: number
  betAmount: number
  balance: number
  fairnessHash?: string
  limits?: SnailRaceLimits
}

export type SnailRaceResultPayload = {
  raceId: string
  winnerIds: number[]
  durationMs: number
  frames: number
  seed: string
  betAmount: number
  betSnailId: number
  clientLog?: {
    positions: number[]
  }
}

export type SnailRaceResultResponse = {
  raceId: string
  verified: boolean
  winnerIds: number[]
  payout: number
  balance: number
  seed: string
  fairnessHash?: string
  serverNote?: string
}
