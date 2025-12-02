<template>
  <transition name="fade">
    <div v-if="isVisible" class="pwa-prompt" role="dialog" aria-live="polite" :style="promptStyle">
      <div class="prompt-content">
        <div class="prompt-text">
          <p class="title">Install PSMO App</p>
          <p class="subtitle">Add to your home screen for faster access to the community.</p>
        </div>
        <div class="prompt-actions">
          <button class="btn secondary" type="button" @click="dismiss">Later</button>
          <button class="btn primary" type="button" @click="install" :disabled="installing">
            {{ installing ? 'Installing...' : 'Install' }}
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
  mobileOnly?: boolean
}>()

const deferredPrompt = ref<BeforeInstallPromptEvent | null>(null)
const isVisible = ref(false)
const installing = ref(false)
const isMobile = ref(false)

const promptStyle = computed(() => ({
  bottom: `calc(${props.bottomOffset ?? 24}px + env(safe-area-inset-bottom, 0px))`,
}))

const updateDeviceState = () => {
  if (typeof window === 'undefined') return
  const coarse = window.matchMedia('(pointer: coarse)').matches
  const ua = navigator.userAgent || ''
  isMobile.value = coarse || /Android|iPhone|iPad|iPod/i.test(ua)
}

const eligible = () => (!props.mobileOnly || isMobile.value)

const handleBeforeInstallPrompt = (event: Event) => {
  event.preventDefault()
  if (!eligible()) return
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
  updateDeviceState()
  window.addEventListener('resize', updateDeviceState)
  window.addEventListener('beforeinstallprompt', handleBeforeInstallPrompt)
  window.addEventListener('appinstalled', handleAppInstalled)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateDeviceState)
  window.removeEventListener('beforeinstallprompt', handleBeforeInstallPrompt)
  window.removeEventListener('appinstalled', handleAppInstalled)
})
</script>

<style scoped>
.pwa-prompt {
  position: fixed;
  left: 50%;
  transform: translateX(-50%);
  width: min(480px, calc(100% - 2rem));
  background: rgba(15, 23, 42, 0.95);
  border-radius: 1rem;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
  color: #f8fafc;
  z-index: 1200;
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
  flex-wrap: wrap;
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
