<template>
  <transition name="fade">
    <div
      v-if="isVisible"
      class="pwa-prompt"
      role="dialog"
      aria-live="polite"
      :style="promptStyle"
    >
      <div class="prompt-content">
        <div class="prompt-text">
          <p class="title">PSMO 앱 설치</p>
          <p class="subtitle">홈 화면에 추가하면 더 빠르게 커뮤니티를 열 수 있어요.</p>
        </div>
        <div class="prompt-actions">
          <button class="btn secondary" type="button" @click="dismiss">나중에</button>
          <button class="btn primary" type="button" @click="install" :disabled="installing">
            {{ installing ? '설치 중...' : '설치하기' }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

type BeforeInstallPromptEvent = Event & {
  prompt: () => Promise<void>
  userChoice: Promise<{ outcome: 'accepted' | 'dismissed'; platform: string }>
}

const props = defineProps<{
  bottomOffset?: number
}>()

const deferredPrompt = ref<BeforeInstallPromptEvent | null>(null)
const isVisible = ref(false)
const installing = ref(false)
const promptStyle = computed(() => ({
  bottom: `calc(${props.bottomOffset ?? 24}px + env(safe-area-inset-bottom, 0px))`,
}))

const handleBeforeInstallPrompt = (event: Event) => {
  event.preventDefault()
  deferredPrompt.value = event as BeforeInstallPromptEvent
  isVisible.value = true
}

const install = async () => {
  if (!deferredPrompt.value) return
  installing.value = true
  try {
    deferredPrompt.value.prompt()
    const choice = await deferredPrompt.value.userChoice
    if (choice.outcome === 'accepted') {
      isVisible.value = false
      deferredPrompt.value = null
    }
  } finally {
    installing.value = false
  }
}

const dismiss = () => {
  isVisible.value = false
}

const handleAppInstalled = () => {
  isVisible.value = false
  deferredPrompt.value = null
}

onMounted(() => {
  window.addEventListener('beforeinstallprompt', handleBeforeInstallPrompt)
  window.addEventListener('appinstalled', handleAppInstalled)
})

onBeforeUnmount(() => {
  window.removeEventListener('beforeinstallprompt', handleBeforeInstallPrompt)
  window.removeEventListener('appinstalled', handleAppInstalled)
})
</script>

<style scoped>
.pwa-prompt {
  position: fixed;
  bottom: 1.5rem;
  left: 50%;
  transform: translateX(-50%);
  width: min(480px, calc(100% - 2rem));
  background: rgba(15, 23, 42, 0.95);
  border-radius: 1rem;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
  color: #f8fafc;
  z-index: 999;
}

.prompt-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.25rem;
}

.prompt-text {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.title {
  font-size: 1rem;
  font-weight: 700;
  margin: 0;
}

.subtitle {
  font-size: 0.9rem;
  color: #cbd5f5;
  margin: 0;
}

.prompt-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.btn {
  border: none;
  border-radius: 999px;
  padding: 0.55rem 1.35rem;
  font-weight: 600;
  cursor: pointer;
}

.btn.secondary {
  background: rgba(255, 255, 255, 0.1);
  color: #e2e8f0;
}

.btn.primary {
  background: linear-gradient(135deg, #ff9d53 0%, #f05365 100%);
  color: white;
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translate(-50%, 10px);
}
</style>
*** End Patch
