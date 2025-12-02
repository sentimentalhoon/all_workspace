<template>
  <section class="features-section">
    <div v-for="feature in features" :key="feature.title" class="feature-card">
      <div class="icon">{{ feature.icon }}</div>
      <h3>{{ feature.title }}</h3>
      <p>{{ feature.description }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { FeatureHighlight } from '@/views/home/constants'

defineProps<{ features: FeatureHighlight[] }>()
</script>
