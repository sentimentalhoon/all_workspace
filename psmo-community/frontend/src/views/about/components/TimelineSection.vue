<script setup lang="ts">
import type { TimelineEntry } from '@/views/about/constants'

defineProps<{ timeline: TimelineEntry[] }>()
</script>

<template>
  <section class="timeline-section">
    <header class="section-header">
      <p class="section-eyebrow">JOURNEY</p>
      <div>
        <h2>함께 걸어온 길</h2>
        <p>커뮤니티의 성장 과정과 앞으로 확장할 실험을 시간 순으로 살펴보세요.</p>
      </div>
    </header>

    <ol class="timeline-list">
      <li v-for="entry in timeline" :key="entry.period" class="timeline-entry">
        <div class="timeline-marker"></div>
        <div class="timeline-content">
          <p class="timeline-period">{{ entry.period }}</p>
          <h3>{{ entry.title }}</h3>
          <p>{{ entry.description }}</p>
        </div>
      </li>
    </ol>
  </section>
</template>
