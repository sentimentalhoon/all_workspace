export const useTelegramAuth = () => {
  const { fetchClient, setAccessToken } = useApiClient();
  const router = useRouter();

  // Config
  const telegramBotId = parseInt(import.meta.env.VITE_TELEGRAM_BOT_ID || "0");
  const telegramBotUsername =
    import.meta.env.VITE_TELEGRAM_BOT_USERNAME || "Psmo_community_bot";

  const loginPending = useState("loginPending", () => false);
  const error = useState<string | null>("telegramError", () => null);

  /**
   * 텔레그램 로그인 처리 함수입니다.
   * 위젯에서 받은 사용자 정보를 백엔드(/api/auth/telegram)로 보냅니다.
   */
  const loginWithTelegram = async (user: any) => {
    loginPending.value = true;
    error.value = null;
    try {
      // 텔레그램 데이터를 폼 데이터 형식(x-www-form-urlencoded)으로 변환합니다.
      const params = new URLSearchParams();
      Object.entries(user).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          params.append(key, String(value));
        }
      });

      // 백엔드에 로그인 요청
      const data = await $fetch<{ token: { accessToken: string }; user: any }>(
        "/api/auth/telegram",
        {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: params.toString(),
        }
      );

      // 받아온 토큰 저장
      setAccessToken(data.token.accessToken);

      return data.user;
    } catch (e: any) {
      error.value = e.message || "Login failed";
      throw e;
    } finally {
      loginPending.value = false;
    }
  };

  return {
    telegramBotId,
    telegramBotUsername,
    loginWithTelegram,
    loginPending,
    error,
  };
};
