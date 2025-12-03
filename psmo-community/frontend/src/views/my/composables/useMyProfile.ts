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

  return {
    user,
    isAuthenticated,
    loading,
    avatarUrl,
    errorMessage,
    loginPending,
    telegramBotUsername,
    handleTelegramLogin,
    handleLogout,
    refreshProfile,
  }
}
