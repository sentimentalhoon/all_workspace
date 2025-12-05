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

  const avatarUrl = computed(
    () => user.value?.photoUrl || 'https://via.placeholder.com/80?text=PSMO',
  )

  const role = computed(() => user.value?.role ?? 'member')
  const score = computed(() => user.value?.score ?? 0)
  const activityLevel = computed(() => user.value?.activityLevel ?? 1)

  const thresholds = [
    { level: 1, min: 0 },
    { level: 2, min: 100 },
    { level: 3, min: 300 },
    { level: 4, min: 700 },
    { level: 5, min: 1500 },
  ]

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
