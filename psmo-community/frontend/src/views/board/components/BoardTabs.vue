<template>
  <div class="board-tabs">
    <button
      v-for="tab in tabs"
      :key="tab.id"
      :class="{ active: modelValue === tab.id }"
      type="button"
      @click="emit('update:modelValue', tab.id)"
    >
      {{ tab.icon }} {{ tab.name }}
    </button>
  </div>
</template>

<script setup lang="ts">
import type { BoardTab } from '../useBoardData'

defineProps({
  tabs: {
    type: Array as () => BoardTab[],
    required: true,
  },
  modelValue: {
    type: String,
    required: true,
  },
})

const emit = defineEmits<{
  (event: 'update:modelValue', value: string): void
}>()
</script>
