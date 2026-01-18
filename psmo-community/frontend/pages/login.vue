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
    const loggedInUser = await loginWithTelegram(user);
    const authStore = useAuthStore();
    authStore.user = loggedInUser; // 스토어 상태 업데이트
    navigateTo("/"); // 로그인 성공하면 홈으로 이동
  } catch (e) {
    console.error("Login error", e);
  }
};

// TypeScript에게 window 객체에 onTelegramAuth 함수가 있을 수 있다고 알려줍니다.
declare global {
  interface Window {
    onTelegramAuth: (user: any) => void;
  }
}

// Load Telegram Widget Script
// 페이지가 열릴 때(Mounted) 텔레그램 위젯 스크립트를 수동으로 추가합니다.
// (Nuxt/Vue에서 외부 스크립트를 로딩하는 일반적인 방법입니다)
onMounted(() => {
  const script = document.createElement("script");
  script.src = "https://telegram.org/js/telegram-widget.js?22";
  script.setAttribute(
    "data-telegram-login",
    import.meta.env.VITE_TELEGRAM_BOT_USERNAME || "Psmo_community_bot",
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

// 페이지를 떠날 때(Unmounted) 전역 함수를 정리합니다. (메모리 누수/충돌 방지)
onUnmounted(() => {
  if (window.onTelegramAuth) {
    // @ts-ignore
    delete window.onTelegramAuth;
  }
});
</script>

<template>
  <div class="login-page">
    <div class="login-card glass-panel bounce-in">
      <div class="logo-area">
        <h1>PSMO</h1>
        <p class="subtitle">PC방 사장님 모임</p>
      </div>

      <p class="desc">서비스 이용을 위해<br />텔레그램으로 로그인해주세요.</p>

      <div v-if="error" class="error-msg">⚠️ {{ error }}</div>

      <div id="telegram-login-container" class="widget-container"></div>

      <div v-if="loginPending" class="loading">
        <span class="spinner"></span> 로그인 처리 중...
      </div>
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
  background: rgba(22, 33, 62, 0.6);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 3rem 2rem;
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  text-align: center;
  max-width: 400px;
  width: 100%;
  color: white;
}

.logo-area {
  margin-bottom: 2rem;
}

.logo-area h1 {
  font-size: 2.5rem;
  font-weight: 800;
  margin: 0;
  background: linear-gradient(135deg, #c5a059 0%, #e94560 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -1px;
}

.subtitle {
  color: #b0b0b0;
  margin: 0;
  font-size: 0.9rem;
}

.desc {
  margin-bottom: 2rem;
  line-height: 1.6;
  color: #e0e0e0;
}

.widget-container {
  margin: 20px 0;
  min-height: 50px;
  display: flex;
  justify-content: center;
}

.error-msg {
  background: rgba(233, 69, 96, 0.2);
  color: #ff6b6b;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 15px;
  font-size: 0.9rem;
}

.loading {
  color: #c5a059;
  margin-top: 15px;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(197, 160, 89, 0.3);
  border-top-color: #c5a059;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.bounce-in {
  animation: bounceIn 0.8s cubic-bezier(0.215, 0.61, 0.355, 1);
}

@keyframes bounceIn {
  0% {
    opacity: 0;
    transform: scale(0.3);
  }
  20% {
    transform: scale(1.1);
  }
  40% {
    transform: scale(0.9);
  }
  60% {
    opacity: 1;
    transform: scale(1.03);
  }
  80% {
    transform: scale(0.97);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
