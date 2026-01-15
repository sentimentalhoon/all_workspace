export const useTelegramAuth = () => {
  const { fetchClient, setAccessToken } = useApiClient();
  const router = useRouter();

  // Config
  const telegramBotId = parseInt(import.meta.env.VITE_TELEGRAM_BOT_ID || "0");
  const telegramBotUsername =
    import.meta.env.VITE_TELEGRAM_BOT_USERNAME || "Psmo_community_bot";

  const loginPending = useState("loginPending", () => false);
  const error = useState<string | null>("telegramError", () => null);

  const loginWithTelegram = async (user: any) => {
    loginPending.value = true;
    error.value = null;
    try {
      // Convert Telegram user object to params as backend expects
      const params = new URLSearchParams();
      Object.entries(user).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          params.append(key, String(value));
        }
      });

      const data = await $fetch<{ token: { accessToken: string }; user: any }>(
        "/api/auth/telegram",
        {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: params.toString(),
        }
      );

      setAccessToken(data.token.accessToken);

      // Redirect or callback
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
