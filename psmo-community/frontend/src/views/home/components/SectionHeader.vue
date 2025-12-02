<template>
  <div class="section-header">
    <h3>{{ title }}</h3>
    <RouterLink v-if="link" :to="link" class="view-more">더보기 ›</RouterLink>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'

defineProps<{ title: string; link?: string }>()
</script>
