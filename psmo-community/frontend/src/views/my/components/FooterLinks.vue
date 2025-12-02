<template>
  <div class="footer-links">
    <template v-for="(link, index) in links" :key="link.label">
      <RouterLink v-if="link.to" :to="link.to">{{ link.label }}</RouterLink>
      <a v-else :href="link.href ?? '#'" target="_blank" rel="noreferrer">
        {{ link.label }}
      </a>
      <span v-if="index < links.length - 1" class="divider">|</span>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { FooterLink } from '@/views/my/constants'
import { RouterLink } from 'vue-router'

defineProps<{ links: FooterLink[] }>()
</script>
