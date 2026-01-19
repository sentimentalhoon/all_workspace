<script setup lang="ts">
import { onMounted, ref } from "vue";

const deferredPrompt = ref<any>(null);
const showInstallPrompt = ref(false);
const isIos = ref(false);

const dismiss = () => {
  showInstallPrompt.value = false;
  // Optional: Save preference to localStorage to not show again for a while
};

const installPwa = async () => {
  if (deferredPrompt.value) {
    deferredPrompt.value.prompt();
    const { outcome } = await deferredPrompt.value.userChoice;
    console.log("User response to the install prompt:", outcome);
    deferredPrompt.value = null;
    showInstallPrompt.value = false;
  }
};

onMounted(() => {
  // 1. Android / Desktop (Chrome/Edge)
  window.addEventListener("beforeinstallprompt", (e) => {
    e.preventDefault();
    deferredPrompt.value = e;
    showInstallPrompt.value = true;
  });

  window.addEventListener("appinstalled", () => {
    console.log("PWA was installed");
    showInstallPrompt.value = false;
  });

  // 2. iOS Detection
  const ua = window.navigator.userAgent;
  const isIosDevice = /iphone|ipad|ipod/i.test(ua);
  const isStandalone =
    window.matchMedia("(display-mode: standalone)").matches ||
    (window.navigator as any).standalone === true;

  if (isIosDevice && !isStandalone) {
    isIos.value = true;
    showInstallPrompt.value = true;
  }
});
</script>

<template>
  <transition name="slide-up">
    <div v-if="showInstallPrompt" class="pwa-install-prompt glass-panel">
      <div class="content">
        <div class="icon">📲</div>
        <div class="text">
          <strong>성피천국 앱 설치</strong>
          <p v-if="isIos">
            '공유' 버튼 <span class="share-icon">⎋</span>을 누르고<br />'홈
            화면에 추가'를 선택하세요.
          </p>
          <p v-else>홈 화면에 추가하여 더 편리하게 이용하세요.</p>
        </div>
      </div>
      <div class="actions">
        <button class="btn btn-text" @click="dismiss">닫기</button>
        <button v-if="!isIos" class="btn btn-primary" @click="installPwa">
          설치하기
        </button>
      </div>
    </div>
  </transition>
</template>

<style scoped lang="scss">
/* Variables */
$primary-color: #1e88e5;
$glass-bg: rgba(22, 33, 62, 0.95);
$glass-border: rgba(255, 255, 255, 0.1);
$text-primary: #ffffff;
$text-secondary: #b0b0b0;

.pwa-install-prompt {
  position: fixed;
  bottom: 80px; /* Above Bottom Nav */
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 32px);
  max-width: 568px;
  background: $glass-bg;
  border: 1px solid $glass-border;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  padding: 16px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: 12px;

  /* Mobile optimization */
  @media (min-width: 600px) {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    bottom: 24px;
    width: auto;
    min-width: 400px;
  }
}

.content {
  display: flex;
  align-items: center;
  gap: 12px;

  .icon {
    font-size: 2rem;
  }

  .text {
    strong {
      display: block;
      color: $text-primary;
      font-size: 1rem;
      margin-bottom: 2px;
    }
    p {
      margin: 0;
      color: $text-secondary;
      font-size: 0.85rem;
    }
  }
}

.actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;

  .btn {
    padding: 8px 16px;
    border-radius: 8px;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    border: none;
    transition: all 0.2s;

    &.btn-text {
      background: transparent;
      color: $text-secondary;
      &:hover {
        background: rgba(255, 255, 255, 0.05);
        color: white;
      }
    }

    &.btn-primary {
      background: $primary-color;
      color: white;
      &:hover {
        background: lighten($primary-color, 10%);
      }
    }
  }
}

/* Animation */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.slide-up-enter-from,
.slide-up-leave-to {
  transform: translate(-50%, 20px);
  opacity: 0;
}
</style>
