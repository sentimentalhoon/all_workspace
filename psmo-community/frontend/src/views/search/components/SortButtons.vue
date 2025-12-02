<template>
  <div class="sort-buttons">
    <button
      v-for="sort in options"
      :key="sort.value"
      type="button"
      :class="{ active: modelValue === sort.value }"
      @click="emit('update:modelValue', sort.value)"
    >
      {{ sort.label }}
    </button>
  </div>
</template>

<script setup lang="ts">
import type { SortOption } from '@/views/search/constants'

defineProps<{ modelValue: string; options: SortOption[] }>()

const emit = defineEmits<{ (e: 'update:modelValue', value: string): void }>()
</script>
