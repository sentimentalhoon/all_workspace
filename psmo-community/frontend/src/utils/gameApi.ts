import type {
  SnailRaceResultPayload,
  SnailRaceResultResponse,
  SnailRaceStartRequest,
  SnailRaceStartResponse,
} from '@/types/games'
import { fetchClient } from './api'

export class GameApiError extends Error {
  constructor(
    message: string,
    public readonly status: number,
    public readonly code?: string,
    public readonly remainingSeconds?: number,
    public readonly raw?: unknown,
  ) {
    super(message)
  }
}

const toJson = async <T>(response: Response): Promise<T> => {
  const data = (await response.json().catch(() => ({}))) as Record<string, unknown>
  if (!response.ok) {
    const message = (data.message as string) ?? '게임 서버 오류가 발생했습니다.'
    const code = data.code as string | undefined
    const remainingSeconds = data.remainingSeconds as number | undefined
    throw new GameApiError(message, response.status, code, remainingSeconds, data)
  }
  return data as T
}

export const startSnailRace = async (
  payload: SnailRaceStartRequest,
): Promise<SnailRaceStartResponse> => {
  const response = await fetchClient('/api/games/snail/races', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  return toJson<SnailRaceStartResponse>(response)
}

export const completeSnailRace = async (
  raceId: string,
  payload: SnailRaceResultPayload,
): Promise<SnailRaceResultResponse> => {
  const response = await fetchClient(`/api/games/snail/races/${raceId}/complete`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  return toJson<SnailRaceResultResponse>(response)
}
