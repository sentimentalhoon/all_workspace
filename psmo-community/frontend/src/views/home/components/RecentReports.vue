<template>
  <section class="recent-section">
    <SectionHeader title="⚠️ 최근 등록된 진상" link="/report" />
    <div class="report-list">
      <button
        v-for="report in reports"
        :key="report.id"
        class="report-item"
        type="button"
        @click="emit('select', report)"
      >
        <div class="report-main">
          <span class="report-type">{{ report.type }}</span>
          <span class="report-location">📍 {{ report.region }} {{ report.pcRoom }}</span>
        </div>
        <div class="report-meta">
          <span :class="['severity-badge', `severity-${report.severity}`]">
            {{ severityLabel(report.severity) }}
          </span>
          <span class="report-time">{{ report.time }}</span>
        </div>
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import SectionHeader from '@/views/home/components/SectionHeader.vue'
import type { ReportItem } from '@/views/home/constants'

defineProps<{ reports: ReportItem[] }>()

const emit = defineEmits<{ (e: 'select', report: ReportItem): void }>()

const severityLabel = (severity: ReportItem['severity']) => {
  if (severity === 'critical') return '매우심각'
  if (severity === 'high') return '심각'
  return '보통'
}
</script>
