<template>
  <div class="posts-tabs">
    <button
      v-for="tab in tabs"
      :key="tab.id"
      :class="{ active: modelValue === tab.id }"
      type="button"
      @click="emit('update:modelValue', tab.id)"
    >
      {{ tab.label }}
      <span v-if="tab.badge" class="tab-badge">{{ tab.badge }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import type { TabId, TabOption } from '@/views/posts/constants'

defineProps<{ modelValue: TabId; tabs: TabOption[] }>()

const emit = defineEmits<{ (e: 'update:modelValue', value: TabId): void }>()
</script>
