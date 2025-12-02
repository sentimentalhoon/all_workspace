<template>
  <div class="stats-section">
    <div v-for="stat in stats" :key="stat.label" class="stat-item">
      <div class="stat-value">{{ stat.value }}</div>
      <div class="stat-label">{{ stat.label }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { UserStat } from '@/views/my/constants'

defineProps<{ stats: UserStat[] }>()
</script>
