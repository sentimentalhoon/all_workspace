# Snail Race API 계약서 (draft)

본 문서는 `/api/games/snail` 계열 API의 요청/응답 스키마, 공정성 시드 규약, 오류 코드를 정의한다. 모든 엔드포인트는 JWT Bearer 인증이 필요하며, `Authorization: Bearer <accessToken>`을 요구한다.

## 1) 레이스 생성 (Start)

- `POST /api/games/snail/races`
- 목적: 베팅 검증, 서버 시드 생성, 트랙/달팽이 파라미터 전달

### Request Body

```json
{
  "snailId": 2,
  "betAmount": 500,
  "clientSeed": "3bbf3d2e-7c2e-4b6c-9bf3-94d2e08f2f21"
}
```

- `snailId` (number, required): 유저가 선택한 달팽이 ID
- `betAmount` (number, required): 포인트 베팅 금액
- `clientSeed` (string, optional): 클라이언트 생성 시드. 서버는 `serverSeed`를 생성 후 두 값을 조합해 `seed` 결정 (권장: `HMAC(serverSecret, serverSeed | clientSeed)` or `serverSeed + ":" + clientSeed`).

### Response 200

```json
{
  "raceId": "sr_20250101_abc123",
  "seed": "a1f5c1e5-2f6a-4a9c-82ba-9b6c1c1c9f1e",
  "snails": [
    { "id": 1, "name": "루나", "color": "#6366f1", "baseSpeed": 1.05 },
    { "id": 2, "name": "모코", "color": "#10b981", "baseSpeed": 1.0 },
    { "id": 3, "name": "보라", "color": "#f59e0b", "baseSpeed": 0.95 }
  ],
  "trackLength": 520,
  "betAmount": 500,
  "balance": 9500,
  "fairnessHash": "sha256:1f4ab...",
  "limits": { "minBet": 100, "maxBet": 5000, "step": 50, "cooldownSeconds": 30 }
}
```

- `seed`: 서버가 내려준 레이스 RNG 시드 (서버+클라 조합)
- `fairnessHash`: `hash(serverSeed)` 또는 `HMAC(serverSecret, serverSeed)` 값 공개용 (serverSeed 자체는 완료 시점까지 비공개)
- `limits`: 서버 정책에 따른 베팅 한도/쿨다운

### Error Codes

- 400 with `code`:
  - `BET_TOO_SMALL` | `BET_TOO_LARGE` | `BET_STEP` | `INSUFFICIENT_BALANCE`
- 429 with `code` `COOLDOWN` and `remainingSeconds`
- 401 `UNAUTHORIZED`
- 500 `SERVER_ERROR`

## 2) 레이스 완료 보고 (Complete)

- `POST /api/games/snail/races/{raceId}/complete`
- 목적: 클라이언트 시뮬레이션 결과 보고, 서버 재현/검증, 포인트 정산

### Request Body

```json
{
  "raceId": "sr_20250101_abc123",
  "winnerIds": [2],
  "durationMs": 8421,
  "frames": 278,
  "seed": "a1f5c1e5-2f6a-4a9c-82ba-9b6c1c1c9f1e",
  "betAmount": 500,
  "betSnailId": 2,
  "clientLog": { "positions": [520, 508, 499] }
}
```

- `clientLog.positions`: 선택적으로 마지막 프레임 기준 각 달팽이 위치(정수) 로그
- 서버는 다음을 검증 후 일치하지 않으면 400을 반환: `betAmount`/`betSnailId`/`seed`가 Start 때 저장된 값과 동일한지

### Response 200

```json
{
  "raceId": "sr_20250101_abc123",
  "verified": true,
  "winnerIds": [2],
  "payout": 950,
  "balance": 10450,
  "seed": "a1f5c1e5-2f6a-4a9c-82ba-9b6c1c1c9f1e",
  "fairnessHash": "sha256:1f4ab...",
  "serverNote": "replayed and matched"
}
```

- `verified`: 서버 재현 결과와 일치 여부. 불일치 시 `verified=false`와 `serverNote` 사유 첨부, 정산 보류/0 처리.
- `payout`: 정산 금액(+/-).

### Error Codes

- 400 with `code`:
  - `RACE_EXPIRED`
  - `BET_MISMATCH` | `SNAIL_MISMATCH` | `SEED_MISMATCH`
  - `BET_TOO_SMALL` | `BET_TOO_LARGE` | `BET_STEP` | `INSUFFICIENT_BALANCE`
- 403 with `code` `RACE_FORBIDDEN`
- 404 `RACE_NOT_FOUND`
- 409 `ALREADY_REPORTED`
- 401 `UNAUTHORIZED`
- 500 `SERVER_ERROR`

## 3) 공정성/시드 규약

- RNG: `mulberry32(hash(serverSeed + ':' + clientSeed))` (현재 구현) — 해시 함수는 SHA-256 후 32bit 축소.
- `fairnessHash`: `hex(sha256(serverSeed))`를 Start 응답에 제공. Complete 이후에도 서버 내부에 `serverSeed`를 유지하여 재현/감사에 사용.
- 서버 저장 데이터: `raceId -> {serverSeed, clientSeed, seed, snailParams, betAmount, snailId, userId, expiresAt}` (TTL 보존)

## 4) 한도/쿨다운/잔액 규칙

- `limits`는 사용자별로 계산해 응답. 서버는 Start 전에 잔액 차감(홀딩)하거나 Complete에서 정산 시 차감/지급.
- `cooldownSeconds`가 남아있으면 Start 시 429와 잔여 쿨다운을 반환.

## 5) 로깅 및 리플레이

- 저장: race(start payload), client log 요약, server replay 결과, 해시, verified flag, 정산 금액, 사용자/시간.
- 선택적 리플레이 API: `/api/games/snail/races/{raceId}` GET (운영 정책에 따라 보호).

## 6) 테스트 시나리오 (권장)

- 정상: 유효한 베팅 -> start -> complete 일치 -> verified=true, 잔액 증가.
- 불일치: seed 또는 winnerIds 조작 -> verified=false, 정산 보류.
- 한도 초과/쿨다운: 400/429 응답 확인.
- 만료: 오래된 raceId 완료 보고 -> 400 INVALID_RESULT.
- 중복 보고: 409 ALREADY_REPORTED.

## 7) 보안/레이트 리밋

- 인증 필수, `auth-jwt`.
- 사용량 제한: IP/사용자 기준 레이트 리밋(예: 60 req/분) 권장.
- CORS: 기존 설정 준수.

(작성 시각: 2025-12-08)
