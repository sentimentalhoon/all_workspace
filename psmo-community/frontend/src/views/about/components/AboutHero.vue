<script setup lang="ts">
import type { AboutStat, HeroContent } from '@/views/about/constants'
import { RouterLink } from 'vue-router'

const props = defineProps<{
  hero: HeroContent
  stats: AboutStat[]
}>()

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
  <section class="about-hero">
    <div class="hero-content">
      <p class="hero-eyebrow">{{ hero.eyebrow }}</p>
      <h1 class="hero-title">
        {{ hero.title }}
      </h1>
      <p class="hero-description">
        {{ hero.description }}
      </p>

      <div class="hero-actions">
        <RouterLink class="hero-button primary" :to="hero.primaryAction.to" @click="handlePrimary">
          {{ hero.primaryAction.label }}
        </RouterLink>
        <RouterLink
          class="hero-button ghost"
          :to="hero.secondaryAction.to"
          @click="handleSecondary"
        >
          {{ hero.secondaryAction.label }}
        </RouterLink>
      </div>
    </div>

    <div class="hero-stats">
      <article v-for="stat in stats" :key="stat.label" class="hero-stat-card">
        <p class="stat-value">{{ stat.value }}</p>
        <p class="stat-label">{{ stat.label }}</p>
        <p class="stat-description">{{ stat.description }}</p>
      </article>
    </div>
  </section>
</template>
