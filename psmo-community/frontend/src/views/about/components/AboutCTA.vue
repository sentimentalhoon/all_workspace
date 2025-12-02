<script setup lang="ts">
import type { CtaContent } from '@/views/about/constants'
import { RouterLink } from 'vue-router'

const props = defineProps<{ cta: CtaContent }>()

const emit = defineEmits<{
  (e: 'primary'): void
  (e: 'secondary'): void
}>()

const handlePrimary = () => {
  emit('primary')
}

const handleSecondary = () => {
  emit('secondary')
}
</script>

<template>
  <section class="cta-section">
    <div class="cta-content">
      <p class="section-eyebrow">NEXT STEP</p>
      <h2>{{ cta.title }}</h2>
      <p>{{ cta.description }}</p>
    </div>

    <div class="cta-actions">
      <RouterLink class="hero-button primary" :to="cta.primaryAction.to" @click="handlePrimary">
        {{ cta.primaryAction.label }}
      </RouterLink>
      <RouterLink class="hero-button ghost" :to="cta.secondaryAction.to" @click="handleSecondary">
        {{ cta.secondaryAction.label }}
      </RouterLink>
    </div>
  </section>
</template>
