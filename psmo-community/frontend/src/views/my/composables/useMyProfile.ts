import { useTelegramAuth } from '@/composables/useTelegramAuth'
import { computed } from 'vue'

export function useMyProfile() {
  const {
    user,
    isAuthenticated,
    loading,
    errorMessage,
    loginPending,
    telegramBotUsername,
    handleTelegramLogin,
    handleLogout,
    refreshProfile,
  } = useTelegramAuth()

  const avatarUrl = computed(() => {
    return user.value?.photoUrl || '/default_avatar.svg'
  })

  const role = computed(() => user.value?.role ?? 'member')
  const score = computed(() => user.value?.score ?? 0)
  const activityLevel = computed(() => user.value?.activityLevel ?? 1)

  /* 
    Backend Logic: Score = 50 * (level - 1) * level
    Level 1: 0
    Level 2: 100
    Level 3: 300
    ...
  */
  const thresholds = Array.from({ length: 100 }, (_, i) => {
    const level = i + 1
    const min = 50 * (level - 1) * level
    return { level, min }
  })

  const nextLevelInfo = computed(() => {
    const current = activityLevel.value
    const currentScore = score.value
    const next = thresholds.find((t) => t.level === current + 1)
    if (!next) return { nextLevel: null, remaining: 0 }
    const remaining = Math.max(0, next.min - currentScore)
    return { nextLevel: next.level, remaining }
  })

  return {
    user,
    isAuthenticated,
    loading,
    avatarUrl,
    role,
    score,
    activityLevel,
    nextLevelInfo,
    errorMessage,
    loginPending,
    telegramBotUsername,
    handleTelegramLogin,
    handleLogout,
    refreshProfile,
  }
}
