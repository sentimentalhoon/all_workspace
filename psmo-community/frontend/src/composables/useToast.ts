import { ref } from 'vue'

export type ToastKind = 'info' | 'success' | 'warn' | 'error'

export type ToastMessage = {
  message: string
  kind?: ToastKind
}

const toastState = ref<ToastMessage | null>(null)
let timer: number | null = null

const clearTimer = () => {
  if (timer) {
    window.clearTimeout(timer)
    timer = null
  }
}

export const useToast = () => {
  const showToast = (toast: ToastMessage, duration = 2200) => {
    clearTimer()
    toastState.value = toast
    timer = window.setTimeout(() => {
      toastState.value = null
      timer = null
    }, duration)
  }

  const hideToast = () => {
    clearTimer()
    toastState.value = null
  }

  return {
    toast: toastState,
    showToast,
    hideToast,
  }
}
