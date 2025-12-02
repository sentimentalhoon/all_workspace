import {
  defaultStats,
  featureHighlights,
  hotPostsSeed,
  quickLinks,
  recentReportsSeed,
  type FeatureHighlight,
  type HotPost,
  type QuickLink,
  type ReportItem,
  type StatSummary,
} from '@/views/home/constants'
import { ref } from 'vue'

export function useHomeContent() {
  const stats = ref<StatSummary>({ ...defaultStats })
  const recentReports = ref<ReportItem[]>([...recentReportsSeed])
  const hotPosts = ref<HotPost[]>([...hotPostsSeed])
  const highlights = ref<FeatureHighlight[]>([...featureHighlights])
  const links = ref<QuickLink[]>([...quickLinks])

  const handleReportClick = (report: ReportItem) => {
    console.info('Report clicked (placeholder)', report)
  }

  const handlePostClick = (post: HotPost) => {
    console.info('Post clicked (placeholder)', post)
  }

  return {
    stats,
    recentReports,
    hotPosts,
    highlights,
    links,
    handleReportClick,
    handlePostClick,
  }
}
