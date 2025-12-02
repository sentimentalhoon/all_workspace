export interface StatSummary {
  totalReports: number
  pcRooms: number
  activeUsers: number
}

export interface QuickLink {
  to: string
  label: string
  icon: string
  variant: 'danger' | 'primary'
}

export interface ReportItem {
  id: number
  type: string
  region: string
  pcRoom: string
  time: string
  severity: 'critical' | 'high' | 'medium'
}

export interface HotPost {
  id: number
  title: string
  comments: number
  likes: number
}

export interface FeatureHighlight {
  icon: string
  title: string
  description: string
}

export const defaultStats: StatSummary = {
  totalReports: 1247,
  pcRooms: 89,
  activeUsers: 3542,
}

export const quickLinks: QuickLink[] = [
  {
    to: '/report',
    label: '진상 등록하기',
    icon: '⚠️',
    variant: 'danger',
  },
  {
    to: '/board',
    label: '게시판 보기',
    icon: '💬',
    variant: 'primary',
  },
]

export const recentReportsSeed: ReportItem[] = [
  {
    id: 1,
    type: '욕설/폭언',
    region: '서울',
    pcRoom: '강남게임존',
    time: '10분 전',
    severity: 'high',
  },
  {
    id: 2,
    type: '미결제',
    region: '경기',
    pcRoom: '수원PC클럽',
    time: '1시간 전',
    severity: 'critical',
  },
  {
    id: 3,
    type: '불결',
    region: '인천',
    pcRoom: '부평게임타운',
    time: '2시간 전',
    severity: 'medium',
  },
]

export const hotPostsSeed: HotPost[] = [
  {
    id: 1,
    title: '요즘 피씨방 물가 너무 오른거 아님?',
    comments: 45,
    likes: 23,
  },
  {
    id: 2,
    title: '강남 근처 괜찮은 피씨방 추천해주세요',
    comments: 32,
    likes: 18,
  },
  {
    id: 3,
    title: '피씨방 알바 10년차가 알려주는 꿀팁',
    comments: 67,
    likes: 89,
  },
]

export const featureHighlights: FeatureHighlight[] = [
  {
    icon: '🖥️',
    title: '피씨방 정보',
    description: '전국 피씨방 정보와 리뷰를 확인하세요',
  },
  {
    icon: '🎮',
    title: '게임 토크',
    description: '다양한 게임에 대해 이야기 나눠보세요',
  },
  {
    icon: '💡',
    title: '팁 & 노하우',
    description: '피씨방 이용 팁과 게임 노하우 공유',
  },
]
