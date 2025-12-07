<template>
  <Teleport to="body">
    <transition name="toast-fade">
      <div v-if="toast" class="global-toast" :data-kind="toast.kind ?? 'info'">
        <span class="dot" />
        <span>{{ toast.message }}</span>
      </div>
    </transition>
  </Teleport>
</template>

<script setup lang="ts">
import { useToast } from '@/composables/useToast'
import { computed } from 'vue'

const { toast } = useToast()
const isVisible = computed(() => !!toast.value)
</script>

<style scoped>
.global-toast {
  position: fixed;
  top: 16px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2000;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.65rem 0.9rem;
  border-radius: 12px;
  background: #0ea5e9;
  color: #f8fafc;
  border: 1px solid #38bdf8;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.2);
  font-weight: 700;
}

.global-toast[data-kind='warn'] {
  background: #f97316;
  border-color: #fb923c;
}

.global-toast[data-kind='error'] {
  background: #ef4444;
  border-color: #fca5a5;
}

.global-toast[data-kind='success'] {
  background: #22c55e;
  border-color: #86efac;
}

.global-toast .dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.8;
}

.toast-fade-enter-active,
.toast-fade-leave-active {
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-8px);
}
</style>
