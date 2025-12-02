<script setup lang="ts">
import type { TeamMember } from '@/views/about/constants'

defineProps<{ members: TeamMember[] }>()
</script>

<template>
  <section class="team-section">
    <header class="section-header">
      <p class="section-eyebrow">TEAM</p>
      <div>
        <h2>사람이 곧 시스템</h2>
        <p>
          운영진과 검수단, 리서처가 매일 데이터를 해석하고 정책으로 연결해 지속 가능한 구조를
          설계합니다.
        </p>
      </div>
    </header>

    <div class="team-grid">
      <article v-for="member in members" :key="member.name" class="team-card">
        <div class="team-avatar" aria-hidden="true">
          {{ member.name.slice(0, 1) }}
        </div>
        <div>
          <p class="team-name">{{ member.name }}</p>
          <p class="team-role">{{ member.role }}</p>
          <p class="team-focus">{{ member.focus }}</p>
          <p class="team-location">{{ member.location }}</p>
        </div>
      </article>
    </div>
  </section>
</template>
