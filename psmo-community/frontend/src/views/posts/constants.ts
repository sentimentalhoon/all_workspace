export type TabId = 'posts' | 'comments' | 'saved'

export interface TabOption {
  id: TabId
  label: string
  badge?: string
}

export interface MyPost {
  id: number
  category: string
  title: string
  content: string
  date: string
  views: number
  comments: number
  likes: number
}

export interface MyComment {
  id: number
  postTitle: string
  content: string
  date: string
  likes: number
}

export interface SavedPost {
  id: number
  category: string
  title: string
  content: string
  author: string
  views: number
  comments: number
}

export interface PostActionPayload {
  type: 'detail' | 'edit' | 'delete' | 'create'
  post?: MyPost
}

export interface CommentActionPayload {
  type: 'detail' | 'delete'
  comment?: MyComment
}

export interface SavedActionPayload {
  type: 'detail' | 'unsave'
  post?: SavedPost
}

export const postTabs: TabOption[] = [
  { id: 'posts', label: '작성한 글' },
  { id: 'comments', label: '댓글' },
  { id: 'saved', label: '저장한 글' },
]

export const initialMyPosts: MyPost[] = [
  {
    id: 1,
    category: '질문',
    title: 'Vue 3 Composition API 사용법',
    content: 'Vue 3의 Composition API를 처음 사용해보는데 setup() 함수 안에서...',
    date: '2024-01-15',
    views: 243,
    comments: 12,
    likes: 24,
  },
  {
    id: 2,
    category: '정보공유',
    title: 'TypeScript 5.0 새로운 기능 정리',
    content: 'TypeScript 5.0에서 추가된 새로운 기능들을 정리해봤습니다...',
    date: '2024-01-10',
    views: 512,
    comments: 28,
    likes: 89,
  },
]

export const initialMyComments: MyComment[] = [
  {
    id: 1,
    postTitle: 'Spring Boot 3.0 마이그레이션 가이드',
    content: '저도 최근에 마이그레이션 했는데, Java 17로 먼저 업그레이드하는 것이 중요합니다.',
    date: '2024-01-14',
    likes: 5,
  },
]

export const initialSavedPosts: SavedPost[] = [
  {
    id: 1,
    category: '프로젝트',
    title: '함께할 사이드 프로젝트 팀원 모집',
    content: 'AI 기반 챗봇 서비스를 개발하려고 하는데...',
    author: '개발자123',
    views: 324,
    comments: 45,
  },
]
