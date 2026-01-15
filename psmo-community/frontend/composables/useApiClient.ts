export const useApiClient = () => {
  const accessToken = useCookie<string | null>("accessToken", {
    maxAge: 60 * 60 * 24 * 7,
    watch: false,
  });

  const isRefreshing = useState("isRefreshing", () => false);

  const setAccessToken = (token: string | null) => {
    accessToken.value = token;
  };

  const getAccessToken = () => accessToken.value;

  const isTokenExpiringSoon = (
    token: string,
    thresholdSeconds = 300
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
      return true;
    }
  };

  const refreshAccessToken = async (): Promise<string | null> => {
    if (isRefreshing.value) return null;

    isRefreshing.value = true;
    try {
      const refreshResponse = await $fetch<{ token: { accessToken: string } }>(
        "/api/auth/refresh",
        {
          method: "POST",
        }
      );

      const newToken = refreshResponse.token.accessToken;
      setAccessToken(newToken);
      return newToken;
    } catch (e) {
      setAccessToken(null);
      return null;
    } finally {
      isRefreshing.value = false;
    }
  };

  const fetchClient = async <T = any>(
    url: string,
    options: any = {}
  ): Promise<T> => {
    const defaultHeaders: Record<string, string> = {};

    if (accessToken.value && isTokenExpiringSoon(accessToken.value)) {
      await refreshAccessToken();
    }

    if (accessToken.value) {
      defaultHeaders["Authorization"] = `Bearer ${accessToken.value}`;
    }

    try {
      const response = await $fetch<T>(url, {
        ...options,
        headers: {
          ...defaultHeaders,
          ...options.headers,
        },
        baseURL: "/api",
        onResponseError: async ({ response }) => {
          if (response.status === 401) {
            const newToken = await refreshAccessToken();
            if (newToken) {
              // Manual retry logic is needed here if using $fetch hook,
              // but $fetch doesn't easily support transparent retry in onResponseError hook modification
              // without re-calling fetch.
              // Simplified: we rely on the catch block below for retry.
            }
          }
        },
      });
      return response;
    } catch (error: any) {
      if (error.statusCode === 401 || error.response?.status === 401) {
        const newToken = await refreshAccessToken();
        if (newToken) {
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
      throw error;
    }
  };

  return {
    fetchClient,
    setAccessToken,
    getAccessToken,
    refreshAccessToken,
  };
};
