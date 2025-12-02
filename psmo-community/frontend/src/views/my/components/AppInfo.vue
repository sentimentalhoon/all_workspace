<template>
  <div class="app-info">
    <p>{{ version }}</p>
    <p>{{ copyright }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{ version: string; copyright: string }>()
</script>
