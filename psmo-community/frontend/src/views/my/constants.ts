export interface UserStat {
  label: string
  value: number
}

export interface MenuItem {
  icon: string
  label: string
  action?: string
}

export interface MenuSection {
  title: string
  items: MenuItem[]
}

export interface FooterLink {
  label: string
  href?: string
  to?: string
}

export const userStats: UserStat[] = [
  { label: '작성 글', value: 0 },
  { label: '댓글', value: 0 },
  { label: '저장한 글', value: 0 },
  { label: '팔로워', value: 0 },
]

export const menuSections: MenuSection[] = [
  {
    title: '활동',
    items: [
      { icon: '🗂️', label: '내가 쓴 글', action: 'my-posts' },
      { icon: '💬', label: '댓글 단 글', action: 'commented-posts' },
      { icon: '⭐', label: '즐겨찾기', action: 'favorites' },
      { icon: '📩', label: '메시지함', action: 'messages' },
    ],
  },
  {
    title: '알림 & 정보',
    items: [
      { icon: '🔔', label: '알림 설정', action: 'notifications' },
      { icon: '📣', label: '공지사항', action: 'announcements' },
      { icon: '📝', label: '피드백', action: 'feedback' },
    ],
  },
  {
    title: '설정',
    items: [
      { icon: '⚙️', label: '환경설정', action: 'preferences' },
      { icon: '🛡️', label: '보안 설정', action: 'security' },
      { icon: '💳', label: '멤버십', action: 'membership' },
    ],
  },
  {
    title: '지원',
    items: [
      { icon: '🙋', label: '도움말', action: 'help' },
      { icon: '❓', label: 'FAQ', action: 'faq' },
      { icon: '📧', label: '문의하기', action: 'contact' },
    ],
  },
]

export const footerLinks: FooterLink[] = [
  { label: '이용약관', href: '#' },
  { label: '개인정보처리방침', href: '#' },
  { label: '관리자', to: '/admin' },
]

export const appInfo = {
  version: 'PSMO Community v1.0.0',
  copyright: 'ⓒ 2024 PSMO. All rights reserved.',
}
