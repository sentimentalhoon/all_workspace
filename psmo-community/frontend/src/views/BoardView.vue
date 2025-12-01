<template>
  <div class="board-view">
    <div class="page-header">
      <h2>💬 게시판</h2>
    </div>

    <div class="board-tabs">
      <button
        v-for="tab in boardTabs"
        :key="tab.id"
        :class="{ active: activeTab === tab.id }"
        @click="activeTab = tab.id"
      >
        {{ tab.icon }} {{ tab.name }}
      </button>
    </div>

    <div class="board-container">
      <!-- 자유게시판 -->
      <div v-if="activeTab === 'free'" class="post-list">
        <div class="write-button-container">
          <button class="btn-write">✍️ 글쓰기</button>
        </div>
        <div v-for="post in freePosts" :key="post.id" class="post-card">
          <div class="post-header">
            <span :class="['category-badge', post.categoryClass]">{{ post.category }}</span>
            <span class="post-author">{{ post.author }}</span>
            <span class="post-time">{{ post.time }}</span>
          </div>
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-preview">{{ post.content }}</p>
          <div class="post-stats">
            <span>👁️ {{ post.views }}</span>
            <span>💬 {{ post.comments }}</span>
            <span>❤️ {{ post.likes }}</span>
          </div>
        </div>
      </div>

      <!-- PC방 정보 -->
      <div v-if="activeTab === 'pcroom'" class="pcroom-list">
        <div class="write-button-container">
          <button class="btn-write">📍 피씨방 추가</button>
        </div>
        <div v-for="pc in pcRooms" :key="pc.id" class="pcroom-card">
          <div class="pcroom-header">
            <h3>{{ pc.name }}</h3>
            <span :class="['rating-badge', pc.ratingClass]">⭐ {{ pc.rating }}</span>
          </div>
          <div class="pcroom-info">
            <p class="location">📍 {{ pc.region }} {{ pc.address }}</p>
            <p class="specs">💻 {{ pc.specs }}</p>
            <p class="price">💰 {{ pc.price }}</p>
          </div>
          <div class="pcroom-tags">
            <span v-for="tag in pc.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="pcroom-stats">
            <span>👁️ {{ pc.views }}</span>
            <span>💬 {{ pc.reviews }}개 리뷰</span>
          </div>
        </div>
      </div>

      <!-- 게임 토크 -->
      <div v-if="activeTab === 'game'" class="post-list">
        <div class="write-button-container">
          <button class="btn-write">🎮 글쓰기</button>
        </div>
        <div v-for="post in gamePosts" :key="post.id" class="post-card">
          <div class="post-header">
            <span class="game-badge">{{ post.game }}</span>
            <span class="post-author">{{ post.author }}</span>
            <span class="post-time">{{ post.time }}</span>
          </div>
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-preview">{{ post.content }}</p>
          <div class="post-stats">
            <span>👁️ {{ post.views }}</span>
            <span>💬 {{ post.comments }}</span>
            <span>❤️ {{ post.likes }}</span>
          </div>
        </div>
      </div>

      <!-- 팁&노하우 -->
      <div v-if="activeTab === 'tip'" class="post-list">
        <div class="write-button-container">
          <button class="btn-write">💡 글쓰기</button>
        </div>
        <div v-for="post in tipPosts" :key="post.id" class="post-card tip-card">
          <div class="post-header">
            <span class="tip-badge">💡 팁</span>
            <span class="post-author">{{ post.author }}</span>
            <span class="post-time">{{ post.time }}</span>
          </div>
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-preview">{{ post.content }}</p>
          <div class="post-stats">
            <span>👁️ {{ post.views }}</span>
            <span>💬 {{ post.comments }}</span>
            <span>👍 추천 {{ post.recommends }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeTab = ref('free')

const boardTabs = [
  { id: 'free', name: '자유게시판', icon: '💬' },
  { id: 'pcroom', name: 'PC방 정보', icon: '🖥️' },
  { id: 'game', name: '게임 토크', icon: '🎮' },
  { id: 'tip', name: '팁&노하우', icon: '💡' },
]

const freePosts = ref([
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

const pcRooms = ref([
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

const gamePosts = ref([
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

const tipPosts = ref([
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
</script>

<style scoped>
.board-view {
  background: #f8f9fa;
  min-height: calc(100vh - 120px);
}

.page-header {
  background: white;
  padding: 1.5rem 1rem;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.page-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #2d3748;
}

.board-tabs {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  background: white;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 73px;
  z-index: 99;
}

.board-tabs button {
  padding: 1rem 0.5rem;
  border: none;
  background: white;
  color: #666;
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: all 0.2s;
}

.board-tabs button.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.board-container {
  padding: 1rem;
}

.write-button-container {
  margin-bottom: 1rem;
}

.btn-write {
  width: 100%;
  padding: 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-write:hover {
  background: #5568d3;
}

.post-card,
.pcroom-card {
  background: white;
  padding: 1rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  flex-wrap: wrap;
}

.category-badge,
.game-badge,
.tip-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
}

.cat-talk {
  background: #667eea;
}

.cat-question {
  background: #48bb78;
}

.game-badge {
  background: #9f7aea;
}

.tip-badge {
  background: #f59e0b;
}

.post-author {
  font-size: 0.85rem;
  color: #666;
}

.post-time {
  font-size: 0.8rem;
  color: #999;
  margin-left: auto;
}

.post-title {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #2d3748;
  font-weight: 600;
}

.post-preview {
  margin: 0 0 1rem 0;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #999;
}

.pcroom-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.pcroom-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #2d3748;
}

.rating-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  color: white;
}

.rating-high {
  background: #f59e0b;
}

.rating-good {
  background: #10b981;
}

.pcroom-info {
  margin-bottom: 1rem;
}

.pcroom-info p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  color: #666;
}

.location {
  color: #667eea !important;
  font-weight: 600;
}

.pcroom-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tag {
  padding: 0.25rem 0.75rem;
  background: #f0f0f0;
  border-radius: 12px;
  font-size: 0.75rem;
  color: #666;
}

.pcroom-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #999;
  padding-top: 1rem;
  border-top: 1px solid #f0f0f0;
}

.tip-card {
  border-left: 4px solid #f59e0b;
}
</style>
