<script setup lang="ts">
const { loginWithTelegram, loginPending, error } = useTelegramAuth();

const handleTelegramAuth = async (user: any) => {
  try {
    await loginWithTelegram(user);
    navigateTo("/");
  } catch (e) {
    console.error("Login error", e);
  }
};

// Load Telegram Widget Script
onMounted(() => {
  const script = document.createElement("script");
  script.src = "https://telegram.org/js/telegram-widget.js?22";
  script.setAttribute(
    "data-telegram-login",
    import.meta.env.VITE_TELEGRAM_BOT_USERNAME || "Psmo_community_bot"
  );
  script.setAttribute("data-size", "large");
  script.setAttribute("data-onauth", "onTelegramAuth(user)");
  script.setAttribute("data-request-access", "write");
  script.async = true;
  document.getElementById("telegram-login-container")?.appendChild(script);

  // global callback for widget
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
