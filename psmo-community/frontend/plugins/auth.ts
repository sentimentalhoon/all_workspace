export default defineNuxtPlugin(async (nuxtApp) => {
  const authStore = useAuthStore();
  const { getAccessToken } = useApiClient();

  // 앱 초기화 시 토큰이 있으면 사용자 정보를 가져옴
  const token = getAccessToken();
  if (token && !authStore.isLoggedIn) {
    await authStore.fetchProfile();
  }
});
