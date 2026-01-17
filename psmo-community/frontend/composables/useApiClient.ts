/**
 * API 호출을 담당하는 훅(Composable)입니다.
 * - 쿠키(Cookie)를 사용해서 토큰을 관리합니다.
 * - Access Token이 만료되면 자동으로 재발급(Refresh) 받습니다.
 */
export const useApiClient = () => {
  // 브라우저 쿠키에 'accessToken'이라는 이름으로 토큰을 저장합니다.
  const accessToken = useCookie<string | null>("accessToken", {
    maxAge: 60 * 60 * 24 * 7, // 7일 동안 유지
    watch: false, // 쿠키 값이 바뀌어도 반응형으로 동작하지 않게 설정 (성능 최적화)
  });

  const isRefreshing = useState("isRefreshing", () => false);

  const setAccessToken = (token: string | null) => {
    accessToken.value = token;
  };

  const getAccessToken = () => accessToken.value;

  /**
   * 토큰이 곧 만료되는지 확인합니다.
   * 토큰 안에 들어있는 만료 시간(exp)을 꺼내서 현재 시간과 비교합니다.
   */
  const isTokenExpiringSoon = (
    token: string,
    thresholdSeconds = 300 // 5분(300초) 남았으면 만료된 것으로 간주
  ): boolean => {
    try {
      const parts = token.split(".");
      const payloadPart = parts[1];
      if (!payloadPart) return true;
      const payload = JSON.parse(
        atob(payloadPart.replace(/-/g, "+").replace(/_/g, "/"))
      );
      if (!payload.exp) return false;
      const now = Math.floor(Date.now() / 1000);
      return payload.exp - now < thresholdSeconds;
    } catch (e) {
      return true; // 에러 나면 그냥 만료된 걸로 침
    }
  };

  /**
   * Access Token이 만료되었을 때, 백엔드에 요청해서 새 토큰을 받아옵니다.
   * (/api/auth/refresh 호출)
   */
  const refreshAccessToken = async (): Promise<string | null> => {
    if (isRefreshing.value) return null; // 이미 재발급 중이면 중복 요청 방지

    isRefreshing.value = true;
    try {
      const refreshResponse = await $fetch<{ token: { accessToken: string } }>(
        "/api/auth/refresh",
        {
          method: "POST", // POST 요청
        }
      );

      const newToken = refreshResponse.token.accessToken;
      setAccessToken(newToken); // 새 토큰으로 갈아끼우기
      return newToken;
    } catch (e) {
      setAccessToken(null); // 실패하면 로그아웃 처리
      return null;
    } finally {
      isRefreshing.value = false;
    }
  };

  /**
   * 실제 API 요청을 보내는 함수입니다. ($fetch 래퍼)
   * - 요청 보내기 전에 토큰이 만료됐는지 검사하고
   * - 헤더(Authorization)에 토큰을 실어서 보냅니다.
   * - 만약 401(인증 실패) 에러가 나면 토큰을 재발급받고 다시 시도합니다.
   */
  const fetchClient = async <T = any>(
    url: string,
    options: any = {}
  ): Promise<T> => {
    const defaultHeaders: Record<string, string> = {};

    // 1. 토큰 만료 검사 (5분 내로 만료되면 미리 재발급)
    if (accessToken.value && isTokenExpiringSoon(accessToken.value)) {
      await refreshAccessToken();
    }

    // 2. 헤더에 토큰 추가 (Bearer Token)
    if (accessToken.value) {
      defaultHeaders["Authorization"] = `Bearer ${accessToken.value}`;
    }

    try {
      // 3. 실제 요청 전송 (baseURL은 /api로 고정)
      const response = await $fetch<T>(url, {
        ...options,
        headers: {
          ...defaultHeaders,
          ...options.headers,
        },
        baseURL: "/api",
        onResponseError: async ({ response }) => {
          // 401 에러 발생 시 처리 로직 (여기서는 생략하고 catch 블록에서 처리)
        },
      });
      return response;
    } catch (error: any) {
      // 4. 에러 발생 시 처리
      // 401(Unauthorized)이면 토큰이 만료된 것이므로 재발급 시도
      if (error.statusCode === 401 || error.response?.status === 401) {
        const newToken = await refreshAccessToken();
        if (newToken) {
          // 재발급 성공하면, 새 토큰으로 다시 요청(Retry)
          defaultHeaders["Authorization"] = `Bearer ${newToken}`;
          return $fetch<T>(url, {
            ...options,
            headers: {
              ...defaultHeaders,
              ...options.headers,
            },
            baseURL: "/api",
          });
        }
      }
      throw error; // 재발급도 실패하면 에러를 그대로 던짐
    }
  };

  return {
    fetchClient,
    setAccessToken,
    getAccessToken,
    refreshAccessToken,
  };
};
