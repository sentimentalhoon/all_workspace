<template>
  <div class="category-chips">
    <button
      v-for="category in categories"
      :key="category"
      type="button"
      :class="{ active: modelValue === category }"
      @click="emit('update:modelValue', category)"
    >
      {{ category }}
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ modelValue: string; categories: string[] }>()

const emit = defineEmits<{ (e: 'update:modelValue', value: string): void }>()
</script>
