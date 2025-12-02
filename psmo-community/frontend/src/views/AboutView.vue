<script setup lang="ts">
/**
 * @fileoverview 서비스 소개 페이지. 미션, 팀, 가치관 콘텐츠를 제공합니다.
 */
import AboutCTA from '@/views/about/components/AboutCTA.vue'
import AboutHero from '@/views/about/components/AboutHero.vue'
import MissionSection from '@/views/about/components/MissionSection.vue'
import TeamGrid from '@/views/about/components/TeamGrid.vue'
import TimelineSection from '@/views/about/components/TimelineSection.vue'
import ValueHighlights from '@/views/about/components/ValueHighlights.vue'
import { useAboutContent } from '@/views/about/composables/useAboutContent'

/**
 * 소개 페이지에 필요한 정적/동적 콘텐츠와 CTA 핸들러를 제공합니다.
 * @see useAboutContent
 */
const {
  hero,
  stats,
  missions,
  values,
  timeline,
  members,
  cta,
  handleHeroPrimary,
  handleHeroSecondary,
  handleCtaPrimary,
  handleCtaSecondary,
} = useAboutContent()
</script>

<template>
  <main class="about">
    <AboutHero
      :hero="hero"
      :stats="stats"
      @primary="handleHeroPrimary"
      @secondary="handleHeroSecondary"
    />
    <MissionSection :missions="missions" />
    <ValueHighlights :values="values" />
    <TimelineSection :timeline="timeline" />
    <TeamGrid :members="members" />
    <AboutCTA :cta="cta" @primary="handleCtaPrimary" @secondary="handleCtaSecondary" />
  </main>
</template>

<style src="@/views/about/styles/about-view.css"></style>
