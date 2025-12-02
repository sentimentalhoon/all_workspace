<template>
  <div class="search-actions">
    <button class="btn-primary" type="button" @click="emit('search')">검색하기</button>
    <button class="btn-secondary" type="button" @click="emit('reset')">초기화</button>
  </div>
</template>

<script setup lang="ts">
const emit = defineEmits<{
  (e: 'search'): void
  (e: 'reset'): void
}>()
</script>
