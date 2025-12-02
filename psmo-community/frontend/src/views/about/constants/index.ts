export interface ActionLink {
  label: string
  to: string
}

export interface HeroContent {
  eyebrow: string
  title: string
  description: string
  primaryAction: ActionLink
  secondaryAction: ActionLink
}

export interface AboutStat {
  label: string
  value: string
  description: string
}

export interface MissionCard {
  title: string
  description: string
  detail: string
}

export interface ValueHighlight {
  icon: string
  title: string
  description: string
}

export interface TimelineEntry {
  period: string
  title: string
  description: string
}

export interface TeamMember {
  name: string
  role: string
  focus: string
  location: string
}

export interface CtaContent {
  title: string
  description: string
  primaryAction: ActionLink
  secondaryAction: ActionLink
}

export const heroContent: HeroContent = {
  eyebrow: 'ABOUT PSMO COMMUNITY',
  title: '피씨방 문화를 바꾸는 연대',
  description:
    'PSMO Community는 전국 피씨방 이용자와 업주가 함께 건강한 공간을 만들기 위해 모인 커뮤니티입니다. 제보, 소통, 학습이 자연스럽게 이어지는 환경을 구축하고 있어요.',
  primaryAction: {
    label: '파트너십 문의',
    to: '/contact',
  },
  secondaryAction: {
    label: '커뮤니티 살펴보기',
    to: '/board',
  },
}

export const heroStats: AboutStat[] = [
  {
    label: '누적 제보',
    value: '12K+',
    description: '실제 이용자 경험 공유',
  },
  {
    label: '제휴 피씨방',
    value: '180+',
    description: '지속 협력 매장',
  },
  {
    label: '활성 멤버십',
    value: '4.2K',
    description: '월간 활동 사용자',
  },
]

export const missionCards: MissionCard[] = [
  {
    title: '투명한 정보 공유',
    description: '모든 제보는 검증 과정을 거쳐 누구나 열람할 수 있도록 공개합니다.',
    detail: '사소한 불편부터 중대한 사고까지 유형별로 정리해 제공해요.',
  },
  {
    title: '안전한 근무 환경',
    description: '알바생과 직원이 겪는 어려움을 빠르게 공유하고 대응합니다.',
    detail: '상황별 대처 가이드와 보호 정책을 함께 만듭니다.',
  },
  {
    title: '건강한 이용 문화',
    description: '손님과 업주가 서로를 존중하는 문화를 확산합니다.',
    detail: '케이스 스터디와 캠페인을 통해 좋은 사례를 확산합니다.',
  },
]

export const valueHighlights: ValueHighlight[] = [
  {
    icon: '🤝',
    title: '공정한 검수',
    description: '운영진과 검수단이 모든 제보를 다층 검토해 악용을 방지합니다.',
  },
  {
    icon: '📚',
    title: '학습 리소스',
    description: '알바 가이드, 업주 운영 팁, 이용자 매너 등 맞춤형 자료를 제공합니다.',
  },
  {
    icon: '🌐',
    title: '지역 연대',
    description: '지역별 모임과 이벤트를 통해 오프라인 네트워크를 확장합니다.',
  },
]

export const timelineEntries: TimelineEntry[] = [
  {
    period: '2019',
    title: '커뮤니티 베타 시작',
    description: '피씨방 알바생 익명 제보 게시판에서 출발했습니다.',
  },
  {
    period: '2021',
    title: '전국 제휴 네트워크 구축',
    description: '50개 매장과 협력해 공용 가이드라인을 도입했습니다.',
  },
  {
    period: '2023',
    title: 'PSMO Community 런칭',
    description: '제보, 통계, 교육을 통합한 플랫폼으로 확장했습니다.',
  },
  {
    period: 'NOW',
    title: '지속 가능한 생태계 실험',
    description: '데이터 기반 정책과 멤버십 프로그램을 실험 중입니다.',
  },
]

export const teamMembers: TeamMember[] = [
  {
    name: '이지은',
    role: '커뮤니티 리드',
    focus: '파트너십, 정책 설계',
    location: '서울',
  },
  {
    name: '박성훈',
    role: '제품 디자이너',
    focus: '경험 설계, 접근성',
    location: '부산',
  },
  {
    name: '정다현',
    role: '데이터 리서처',
    focus: '제보 분석, 지표 수립',
    location: '대전',
  },
  {
    name: '한지민',
    role: '운영 매니저',
    focus: '검수 프로세스, 모더레이션',
    location: '인천',
  },
]

export const ctaContent: CtaContent = {
  title: 'PSMO와 함께 변화를 만들고 싶다면?',
  description:
    '지역 모임 개최, 교육 프로그램 공동 기획, 데이터 리서치 등 다양한 방법으로 협력할 수 있어요.',
  primaryAction: {
    label: '콜라보 제안하기',
    to: '/contact',
  },
  secondaryAction: {
    label: '커뮤니티 합류하기',
    to: '/signup',
  },
}
