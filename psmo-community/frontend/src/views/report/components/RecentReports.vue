<template>
  <div class="recent-reports">
    <h3>최근 등록된 진상</h3>
    <div class="report-list">
      <div v-for="report in reports" :key="report.id" class="report-card">
        <div class="report-header">
          <span :class="['type-badge', report.typeClass]">{{ report.type }}</span>
          <span class="report-region">{{ report.region }}</span>
          <span class="report-date">{{ report.date }}</span>
        </div>
        <div class="report-info">
          <p class="pc-room-name" v-if="report.pcRoom">📍 {{ report.pcRoom }}</p>
          <p class="report-content">{{ report.content }}</p>
          <p class="report-characteristic" v-if="report.characteristic">
            👤 특징: {{ report.characteristic }}
          </p>
        </div>
        <div class="report-footer">
          <span :class="['severity-indicator', report.severityClass]">
            {{ report.severityLabel }}
          </span>
          <div class="report-stats">
            <span>👁️ {{ report.views }}</span>
            <span>💬 {{ report.comments }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
type RecentReport = {
  id: number
  type: string
  typeClass: string
  region: string
  pcRoom?: string
  date: string
  characteristic?: string
  content: string
  severityLabel: string
  severityClass: string
  views: number
  comments: number
}

defineProps<{
  reports: RecentReport[]
}>()
</script>
