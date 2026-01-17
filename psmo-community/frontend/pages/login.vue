<script setup lang="ts">
/**
 * 로그인 페이지입니다.
 * 텔레그램 공식 위젯(Login Widget)을 불러와서 화면에 보여줍니다.
 */
const { loginWithTelegram, loginPending, error } = useTelegramAuth();

/**
 * 위젯에서 로그인이 완료되면 이 함수가 실행됩니다.
 * 받아온 정보를 가지고 백엔드에 로그인 요청을 보냅니다.
 */
const handleTelegramAuth = async (user: any) => {
  try {
    await loginWithTelegram(user);
    navigateTo("/"); // 로그인 성공하면 홈으로 이동
  } catch (e) {
    console.error("Login error", e);
  }
};

// Load Telegram Widget Script
// 페이지가 열릴 때(Mounted) 텔레그램 위젯 스크립트를 수동으로 추가합니다.
// (Nuxt/Vue에서 외부 스크립트를 로딩하는 일반적인 방법입니다)
onMounted(() => {
  const script = document.createElement("script");
  script.src = "https://telegram.org/js/telegram-widget.js?22";
  script.setAttribute(
    "data-telegram-login",
    import.meta.env.VITE_TELEGRAM_BOT_USERNAME || "Psmo_community_bot"
  );
  script.setAttribute("data-size", "large");

  // 로그인 완료 시 실행할 함수 이름 (전역 함수여야 함)
  script.setAttribute("data-onauth", "onTelegramAuth(user)");
  script.setAttribute("data-request-access", "write"); // 봇이 사용자에게 메시지를 보낼 수 있게 권한 요청
  script.async = true;
  document.getElementById("telegram-login-container")?.appendChild(script);

  // 텔레그램 위젯은 HTML 안에서 'onTelegramAuth'라는 함수를 찾습니다.
  // 그래서 window 객체(전역)에 함수를 등록해줍니다.
  window.onTelegramAuth = (user: any) => {
    handleTelegramAuth(user);
  };
});
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h2>Welcome to PSMO</h2>
      <p>Please log in with Telegram to continue.</p>

      <div v-if="error" class="error-msg">{{ error }}</div>

      <div id="telegram-login-container" class="widget-container"></div>

      <div v-if="loginPending" class="loading">Logging in...</div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}

.login-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
  max-width: 400px;
  width: 100%;
}

.widget-container {
  margin: 20px 0;
  min-height: 50px;
}

.error-msg {
  color: red;
  margin-bottom: 10px;
  font-size: 0.9rem;
}

.loading {
  color: #666;
  margin-top: 10px;
}
</style>
