import { defineStore } from "pinia";

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

  const fetchProfile = async () => {
    try {
      const data = await fetchClient<{ user: User }>("/me");
      user.value = data.user;
    } catch (e) {
      user.value = null;
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
