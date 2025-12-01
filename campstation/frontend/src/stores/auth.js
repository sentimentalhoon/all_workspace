import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useAuthStore = defineStore("auth", () => {
  // State
  const user = ref(null);
  const token = ref(localStorage.getItem("token") || null);
  const refreshToken = ref(localStorage.getItem("refreshToken") || null);

  // Getters
  const isAuthenticated = computed(() => !!token.value);
  const userName = computed(() => user.value?.name || "Guest");
  const userEmail = computed(() => user.value?.email || "");

  // Actions
  function setToken(newToken, newRefreshToken = null) {
    token.value = newToken;
    if (newToken) {
      localStorage.setItem("token", newToken);
    } else {
      localStorage.removeItem("token");
    }

    if (newRefreshToken) {
      refreshToken.value = newRefreshToken;
      localStorage.setItem("refreshToken", newRefreshToken);
    }
  }

  function setUser(userData) {
    user.value = userData;
  }

  function login(userData, authToken, authRefreshToken = null) {
    setUser(userData);
    setToken(authToken, authRefreshToken);
  }

  function logout() {
    user.value = null;
    token.value = null;
    refreshToken.value = null;
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
  }

  function updateUser(userData) {
    user.value = { ...user.value, ...userData };
  }

  return {
    // State
    user,
    token,
    refreshToken,
    // Getters
    isAuthenticated,
    userName,
    userEmail,
    // Actions
    setToken,
    setUser,
    login,
    logout,
    updateUser,
  };
});
