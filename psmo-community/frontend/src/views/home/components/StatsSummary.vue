<template>
  <section class="stats-section">
    <div class="stat-card">
      <div class="stat-number">{{ stats.totalReports.toLocaleString() }}</div>
      <div class="stat-label">진상 등록</div>
    </div>
    <div class="stat-card">
      <div class="stat-number">{{ stats.pcRooms }}</div>
      <div class="stat-label">피씨방</div>
    </div>
    <div class="stat-card">
      <div class="stat-number">{{ stats.activeUsers.toLocaleString() }}</div>
      <div class="stat-label">활성 유저</div>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { StatSummary } from '@/views/home/constants'

defineProps<{ stats: StatSummary }>()
</script>
