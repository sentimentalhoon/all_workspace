<template>
  <section class="hero">
    <div class="hero-copy">
      <p class="badge">{{ badge }}</p>
      <h1>{{ title }}</h1>
      <p class="summary">{{ summary }}</p>
      <div class="hero-actions">
        <a class="cta primary" :href="primaryCta.href" target="_blank" rel="noreferrer">
          {{ primaryCta.label }}
        </a>
        <button class="cta ghost" type="button" @click="emit('scroll-to-widget')">
          {{ secondaryCtaLabel }}
        </button>
      </div>
      <div class="hero-metrics">
        <span v-for="metric in metrics" :key="metric">{{ metric }}</span>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const props = defineProps({
  badge: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  summary: {
    type: String,
    required: true,
  },
  primaryCta: {
    type: Object as () => { label: string; href: string },
    required: true,
  },
  secondaryCtaLabel: {
    type: String,
    required: true,
  },
  metrics: {
    type: Array as () => string[],
    required: true,
  },
})

const emit = defineEmits<{
  (event: 'scroll-to-widget'): void
}>()

// This hero is presentational; actions are delegated to the parent through emits.
</script>
