<script setup lang="ts">
import type { MissionCard } from '@/views/about/constants'

defineProps<{ missions: MissionCard[] }>()
</script>

<template>
  <section class="mission-section">
    <header class="section-header">
      <p class="section-eyebrow">OUR MISSION</p>
      <div>
        <h2>PSMO를 움직이는 원칙</h2>
        <p>
          제보, 데이터, 교육이 선순환하도록 설계된 미션 체계로 신뢰할 수 있는 협력 구조를
          만들어갑니다.
        </p>
      </div>
    </header>

    <div class="mission-grid">
      <article v-for="mission in missions" :key="mission.title" class="mission-card">
        <h3>{{ mission.title }}</h3>
        <p class="mission-description">{{ mission.description }}</p>
        <p class="mission-detail">{{ mission.detail }}</p>
      </article>
    </div>
  </section>
</template>
