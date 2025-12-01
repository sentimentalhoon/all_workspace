import apiClient from "./axios";

export const authApi = {
  // 로그인
  login(email, password) {
    return apiClient.post("/api/auth/login", { email, password });
  },

  // 회원가입
  register(name, email, password) {
    return apiClient.post("/api/auth/register", { name, email, password });
  },

  // 토큰 갱신
  refreshToken(refreshToken) {
    return apiClient.post("/api/auth/refresh", { refreshToken });
  },

  // 로그아웃
  logout() {
    return apiClient.post("/api/auth/logout");
  },

  // 현재 사용자 정보 조회
  getCurrentUser() {
    return apiClient.get("/api/auth/me");
  },
};
