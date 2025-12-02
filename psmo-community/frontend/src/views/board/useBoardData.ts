import { ref } from 'vue'

export type BoardTab = {
  id: string
  name: string
  icon: string
}

type BoardPostBase = {
  id: number
  title: string
  content: string
  author: string
  time: string
  views: number
  comments: number
  category?: string
  categoryClass?: string
  game?: string
  likes?: number
  recommends?: number
}

export type FreePost = BoardPostBase & {
  category: string
  categoryClass: string
  likes: number
}

export type GamePost = BoardPostBase & {
  game: string
  likes: number
}

export type TipPost = BoardPostBase & {
  recommends: number
}

export type PcRoom = {
  id: number
  name: string
  region: string
  address: string
  specs: string
  price: string
  rating: number
  ratingClass: string
  tags: string[]
  views: number
  reviews: number
}

export const useBoardData = () => {
  const activeTab = ref('free')

  const boardTabs: BoardTab[] = [
    { id: 'free', name: '자유게시판', icon: '💬' },
    { id: 'pcroom', name: 'PC방 정보', icon: '🖥️' },
    { id: 'game', name: '게임 토크', icon: '🎮' },
    { id: 'tip', name: '팁&노하우', icon: '💡' },
  ]

  const freePosts = ref<FreePost[]>([
    {
      id: 1,
      category: '잡담',
      categoryClass: 'cat-talk',
      title: '요즘 피씨방 물가 너무 오른거 아님?',
      content: '우리동네 피씨방 시간당 2500원 ㅋㅋ 예전엔 1500원이었는데...',
      author: '게이머123',
      time: '10분 전',
      views: 45,
      comments: 8,
      likes: 12,
    },
    {
      id: 2,
      category: '질문',
      categoryClass: 'cat-question',
      title: '강남 근처 괜찮은 피씨방 추천해주세요',
      content: '강남역 근처에서 롤 할만한 피씨방 찾고있습니다. 사양 좋은곳으로요',
      author: '롤러',
      time: '1시간 전',
      views: 123,
      comments: 15,
      likes: 5,
    },
  ])

  const pcRooms = ref<PcRoom[]>([
    {
      id: 1,
      name: '강남게임존',
      region: '서울',
      address: '강남구 역삼동',
      specs: 'RTX 4070 / i7-13700K / 32GB RAM',
      price: '시간당 2,500원',
      rating: 4.5,
      ratingClass: 'rating-high',
      tags: ['고사양', '24시간', '음식주문'],
      views: 1234,
      reviews: 45,
    },
    {
      id: 2,
      name: '홍대게임클럽',
      region: '서울',
      address: '마포구 서교동',
      specs: 'RTX 4060 / i5-13400F / 16GB RAM',
      price: '시간당 2,000원',
      rating: 4.2,
      ratingClass: 'rating-good',
      tags: ['깔끔함', '넓은좌석', '주차가능'],
      views: 892,
      reviews: 32,
    },
  ])

  const gamePosts = ref<GamePost[]>([
    {
      id: 1,
      game: '🎮 LOL',
      title: '티어 올리는 꿀팁 공유',
      content: '피씨방에서 랭크 돌릴때 꼭 지켜야할 것들 정리해봤습니다...',
      author: '다이아유저',
      time: '3시간 전',
      views: 234,
      comments: 28,
      likes: 56,
    },
    {
      id: 2,
      game: '⚔️ 로스트아크',
      title: '피씨방 버프 받고 레이드 가자',
      content: '피씨방에서 하면 버프 받아서 효율 좋음',
      author: '로아러',
      time: '5시간 전',
      views: 156,
      comments: 12,
      likes: 23,
    },
  ])

  const tipPosts = ref<TipPost[]>([
    {
      id: 1,
      title: '피씨방에서 게임 최적화 설정하는 법',
      content: '피씨방 컴퓨터에서 게임 프레임 최대로 뽑는 세팅 방법 알려드립니다...',
      author: '컴린이탈출',
      time: '1일 전',
      views: 567,
      comments: 34,
      recommends: 89,
    },
    {
      id: 2,
      title: '피씨방 알바 10년차가 알려주는 꿀팁',
      content: '피씨방 이용할 때 알면 좋은 것들, 알바 입장에서 정리해봤어요',
      author: '피방알바',
      time: '2일 전',
      views: 892,
      comments: 67,
      recommends: 145,
    },
  ])

  return {
    activeTab,
    boardTabs,
    freePosts,
    pcRooms,
    gamePosts,
    tipPosts,
  }
}
