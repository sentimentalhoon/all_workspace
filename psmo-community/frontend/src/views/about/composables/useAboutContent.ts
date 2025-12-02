import {
  ctaContent,
  heroContent,
  heroStats,
  missionCards,
  teamMembers,
  timelineEntries,
  valueHighlights,
  type AboutStat,
  type CtaContent,
  type HeroContent,
  type MissionCard,
  type TeamMember,
  type TimelineEntry,
  type ValueHighlight,
} from '@/views/about/constants'
import { ref } from 'vue'

export function useAboutContent() {
  const hero = ref<HeroContent>({ ...heroContent })
  const stats = ref<AboutStat[]>([...heroStats])
  const missions = ref<MissionCard[]>([...missionCards])
  const values = ref<ValueHighlight[]>([...valueHighlights])
  const timeline = ref<TimelineEntry[]>([...timelineEntries])
  const members = ref<TeamMember[]>([...teamMembers])
  const cta = ref<CtaContent>({ ...ctaContent })

  const handleHeroPrimary = () => {
    console.info('Primary hero action clicked (placeholder)')
  }

  const handleHeroSecondary = () => {
    console.info('Secondary hero action clicked (placeholder)')
  }

  const handleCtaPrimary = () => {
    console.info('CTA primary action clicked (placeholder)')
  }

  const handleCtaSecondary = () => {
    console.info('CTA secondary action clicked (placeholder)')
  }

  return {
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
  }
}
