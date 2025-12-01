import router from "@/router";
import { useAuthStore } from "@/stores/auth";
import axios from "axios";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;

const apiClient = axios.create({
  baseURL: apiBaseUrl,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor - 요청에 토큰 자동 추가
apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - 토큰 만료 시 자동 갱신
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // 401 에러이고, 재시도하지 않은 요청인 경우
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const authStore = useAuthStore();

      // refresh token이 있으면 토큰 갱신 시도
      if (authStore.refreshToken) {
        try {
          const response = await axios.post(`${apiBaseUrl}/api/auth/refresh`, {
            refreshToken: authStore.refreshToken,
          });

          const { token, refreshToken } = response.data;
          authStore.setToken(token, refreshToken);

          // 원래 요청 재시도
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return apiClient(originalRequest);
        } catch (refreshError) {
          // refresh token도 만료된 경우 로그아웃
          authStore.logout();
          router.push("/login");
          return Promise.reject(refreshError);
        }
      } else {
        // refresh token이 없으면 로그아웃
        authStore.logout();
        router.push("/login");
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
