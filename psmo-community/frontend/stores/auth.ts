import { defineStore } from "pinia";

/**
 * 사용자 정보(전역 상태)를 관리하는 저장소(Store)입니다.
 * 앱 어디서든 useAuthStore()를 불러서 내 정보(user)를 꺼내볼 수 있습니다.
 */

export interface User {
  id: number;
  username: string;
  displayName: string;
  role: "USER" | "ADMIN";
  score: number;
  photoUrl?: string;
}

export const useAuthStore = defineStore("auth", () => {
  const user = useState<User | null>("user", () => null);
  const { fetchClient, setAccessToken, refreshAccessToken } = useApiClient();
  const isLoggedIn = computed(() => !!user.value);

  /**
   * 내 정보 가져오기 (/api/me)
   */
  const fetchProfile = async () => {
    try {
      const data = await fetchClient<{ user: User }>("/me");
      user.value = data.user;
    } catch (e) {
      user.value = null; // 실패하면 비로그인 상태
    }
  };

  const logout = async () => {
    try {
      await $fetch("/api/auth/logout", { method: "POST" });
    } catch (e) {}
    setAccessToken(null);
    user.value = null;
    navigateTo("/login");
  };

  return {
    user,
    isLoggedIn,
    fetchProfile,
    logout,
  };
});
