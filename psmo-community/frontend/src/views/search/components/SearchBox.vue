<template>
  <div class="search-box">
    <input
      :value="modelValue"
      type="text"
      placeholder="게시글, 태그, 작성자 검색..."
      @input="emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      @keyup.enter="emit('search')"
    />
    <button type="button" @click="emit('search')">검색</button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ modelValue: string }>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'search'): void
}>()
</script>
