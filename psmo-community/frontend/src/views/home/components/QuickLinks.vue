<template>
  <section class="quick-links">
    <RouterLink
      v-for="link in links"
      :key="link.label"
      :to="link.to"
      class="quick-link"
      :class="link.variant"
    >
      <span class="link-icon">{{ link.icon }}</span>
      <span class="link-text">{{ link.label }}</span>
      <span class="link-arrow">›</span>
    </RouterLink>
  </section>
</template>

<script setup lang="ts">
import type { QuickLink } from '@/views/home/constants'
import { RouterLink } from 'vue-router'

defineProps<{ links: QuickLink[] }>()
</script>
