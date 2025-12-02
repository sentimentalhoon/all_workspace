import { onBeforeUnmount, onMounted } from 'vue'

const WIDGET_CONTAINER_ID = 'telegram-discussion'
const WIDGET_SCRIPT_ID = 'telegram-widget-script'
const WIDGET_SECTION_ID = 'chat-widget'

export const useTelegramWidget = () => {
  const mountWidget = () => {
    const container = document.getElementById(WIDGET_CONTAINER_ID)
    if (!container) {
      return
    }
    container.innerHTML = ''

    const script = document.createElement('script')
    script.id = WIDGET_SCRIPT_ID
    script.src = 'https://telegram.org/js/telegram-widget.js?22'
    script.async = true
    script.setAttribute('data-telegram-discussion', 'psmocommunity/4')
    script.setAttribute('data-comments-limit', '50')
    script.setAttribute('data-color', '#ff8a4c')
    script.setAttribute('data-dark-color', '#f05365')
    script.setAttribute('data-colorful', '1')
    container.appendChild(script)
  }

  const removeWidget = () => {
    document.getElementById(WIDGET_SCRIPT_ID)?.remove()
    const container = document.getElementById(WIDGET_CONTAINER_ID)
    if (container) {
      container.innerHTML = ''
    }
  }

  const reloadWidget = () => {
    removeWidget()
    setTimeout(mountWidget, 50)
  }

  const scrollToWidget = () => {
    document.getElementById(WIDGET_SECTION_ID)?.scrollIntoView({ behavior: 'smooth' })
  }

  onMounted(() => {
    mountWidget()
  })

  onBeforeUnmount(() => {
    removeWidget()
  })

  return {
    widgetContainerId: WIDGET_CONTAINER_ID,
    widgetSectionId: WIDGET_SECTION_ID,
    mountWidget,
    reloadWidget,
    scrollToWidget,
  }
}
