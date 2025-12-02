<script setup lang="ts">
/**
 * @fileoverview 홈 대시보드. 주요 카드와 하이라이트 섹션을 구성합니다.
 */
import FeatureHighlights from '@/views/home/components/FeatureHighlights.vue'
import HotPosts from '@/views/home/components/HotPosts.vue'
import QuickLinks from '@/views/home/components/QuickLinks.vue'
import RecentReports from '@/views/home/components/RecentReports.vue'
import StatsSummary from '@/views/home/components/StatsSummary.vue'
import WelcomeCard from '@/views/home/components/WelcomeCard.vue'
import { useHomeContent } from '@/views/home/composables/useHomeContent'

/**
 * 홈 화면 카드에 필요한 데이터와 클릭 핸들러를 제공합니다.
 * @see useHomeContent
 */
const { stats, links, recentReports, hotPosts, highlights, handleReportClick, handlePostClick } =
  useHomeContent()
</script>

<template>
  <main class="home">
    <WelcomeCard />
    <StatsSummary :stats="stats" />
    <QuickLinks :links="links" />
    <RecentReports :reports="recentReports" @select="handleReportClick" />
    <HotPosts :posts="hotPosts" @select="handlePostClick" />
    <FeatureHighlights :features="highlights" />
  </main>
</template>

<style src="@/views/home/styles/home-view.css"></style>
