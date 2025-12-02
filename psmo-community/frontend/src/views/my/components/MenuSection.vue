<template>
  <div class="menu-section">
    <h3>{{ title }}</h3>
    <div class="menu-items">
      <button
        v-for="item in items"
        :key="item.label"
        class="menu-item"
        type="button"
        @click="emit('select', item.action ?? item.label)"
      >
        <span class="menu-icon">{{ item.icon }}</span>
        <span class="menu-label">{{ item.label }}</span>
        <span class="menu-arrow">›</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { MenuItem } from '@/views/my/constants'

defineProps<{
  title: string
  items: MenuItem[]
}>()

const emit = defineEmits<{
  (e: 'select', action: string): void
}>()
</script>
