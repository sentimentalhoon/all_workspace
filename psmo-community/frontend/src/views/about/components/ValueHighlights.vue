<script setup lang="ts">
import type { ValueHighlight } from '@/views/about/constants'

defineProps<{ values: ValueHighlight[] }>()
</script>

<template>
  <section class="value-section">
    <header class="section-header">
      <p class="section-eyebrow">HOW WE OPERATE</p>
      <div>
        <h2>PSMO가 제공하는 가치</h2>
        <p>
          정보 검수부터 지역 연대까지, 구성원이 체감할 수 있는 운영 원칙을 유지하며 모든 참가자가
          안심하고 활동할 수 있도록 지원합니다.
        </p>
      </div>
    </header>

    <div class="value-grid">
      <article v-for="value in values" :key="value.title" class="value-card">
        <span class="value-icon">{{ value.icon }}</span>
        <h3>{{ value.title }}</h3>
        <p>{{ value.description }}</p>
      </article>
    </div>
  </section>
</template>
