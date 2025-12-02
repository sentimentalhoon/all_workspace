<template>
  <section class="widget-wrapper" :id="sectionId">
    <header>
      <div>
        <h2>{{ title }}</h2>
        <p>{{ subtitle }}</p>
      </div>
      <button type="button" class="refresh-btn" @click="emit('reload')">{{ reloadLabel }}</button>
    </header>
    <div :id="containerId" class="widget-shell">
      <p class="loading-text">{{ loadingText }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
const props = defineProps({
  title: {
    type: String,
    required: true,
  },
  subtitle: {
    type: String,
    required: true,
  },
  containerId: {
    type: String,
    required: true,
  },
  sectionId: {
    type: String,
    required: true,
  },
  reloadLabel: {
    type: String,
    default: '새로고침',
  },
  loadingText: {
    type: String,
    default: '채팅 위젯을 불러오는 중입니다...',
  },
})

const emit = defineEmits<{
  (event: 'reload'): void
}>()
</script>
