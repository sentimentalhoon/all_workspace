<template>
  <div class="posts-view">
    <div class="page-header">
      <h2>✍️ 내가 쓴 글</h2>
    </div>

    <div class="tabs">
      <button :class="{ active: activeTab === 'posts' }" @click="activeTab = 'posts'">
        작성한 글
      </button>
      <button :class="{ active: activeTab === 'comments' }" @click="activeTab = 'comments'">
        댓글
      </button>
      <button :class="{ active: activeTab === 'saved' }" @click="activeTab = 'saved'">
        저장한 글
      </button>
    </div>

    <div class="posts-container">
      <div v-if="activeTab === 'posts'" class="posts-list">
        <div v-for="post in myPosts" :key="post.id" class="post-card">
          <div class="post-header">
            <span class="category-badge">{{ post.category }}</span>
            <span class="post-date">{{ post.date }}</span>
          </div>
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-preview">{{ post.content }}</p>
          <div class="post-stats">
            <span>👁️ {{ post.views }}</span>
            <span>💬 {{ post.comments }}</span>
            <span>❤️ {{ post.likes }}</span>
          </div>
          <div class="post-actions">
            <button class="btn-detail">상세보기</button>
            <button class="btn-edit">수정</button>
            <button class="btn-delete">삭제</button>
          </div>
        </div>

        <div v-if="myPosts.length === 0" class="empty-state">
          <p>작성한 글이 없습니다</p>
          <button class="btn-create">글 작성하기</button>
        </div>
      </div>

      <div v-if="activeTab === 'comments'" class="comments-list">
        <div v-for="comment in myComments" :key="comment.id" class="comment-card">
          <div class="comment-header">
            <span class="post-title-ref">{{ comment.postTitle }}</span>
            <span class="comment-date">{{ comment.date }}</span>
          </div>
          <p class="comment-content">{{ comment.content }}</p>
          <div class="comment-stats">
            <span>❤️ {{ comment.likes }}</span>
          </div>
          <div class="comment-actions">
            <button class="btn-detail">원글 보기</button>
            <button class="btn-delete">삭제</button>
          </div>
        </div>

        <div v-if="myComments.length === 0" class="empty-state">
          <p>작성한 댓글이 없습니다</p>
        </div>
      </div>

      <div v-if="activeTab === 'saved'" class="saved-list">
        <div v-for="saved in savedPosts" :key="saved.id" class="post-card">
          <div class="post-header">
            <span class="category-badge">{{ saved.category }}</span>
            <span class="author">{{ saved.author }}</span>
          </div>
          <h3 class="post-title">{{ saved.title }}</h3>
          <p class="post-preview">{{ saved.content }}</p>
          <div class="post-stats">
            <span>👁️ {{ saved.views }}</span>
            <span>💬 {{ saved.comments }}</span>
          </div>
          <div class="post-actions">
            <button class="btn-detail">상세보기</button>
            <button class="btn-unsave">저장 취소</button>
          </div>
        </div>

        <div v-if="savedPosts.length === 0" class="empty-state">
          <p>저장한 글이 없습니다</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeTab = ref('posts')

const myPosts = ref([
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
])

const myComments = ref([
  {
    id: 1,
    postTitle: 'Spring Boot 3.0 마이그레이션 가이드',
    content: '저도 최근에 마이그레이션 했는데, Java 17로 먼저 업그레이드하는 것이 중요합니다.',
    date: '2024-01-14',
    likes: 5,
  },
])

const savedPosts = ref([
  {
    id: 1,
    category: '프로젝트',
    title: '함께할 사이드 프로젝트 팀원 모집',
    content: 'AI 기반 챗봇 서비스를 개발하려고 하는데...',
    author: '개발자123',
    views: 324,
    comments: 45,
  },
])
</script>

<style scoped>
.posts-view {
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

.tabs {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  background: white;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 73px;
  z-index: 99;
}

.tabs button {
  padding: 1rem;
  border: none;
  background: white;
  color: #666;
  font-weight: 600;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: all 0.2s;
}

.tabs button.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.posts-container {
  padding: 1rem;
}

.post-card,
.comment-card {
  background: white;
  padding: 1rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.post-header,
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.category-badge {
  background: #667eea;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.post-date,
.comment-date {
  font-size: 0.85rem;
  color: #999;
}

.post-title {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  color: #2d3748;
}

.post-preview,
.comment-content {
  margin: 0 0 1rem 0;
  color: #666;
  font-size: 0.95rem;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-stats,
.comment-stats {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #666;
}

.post-actions,
.comment-actions {
  display: flex;
  gap: 0.5rem;
}

.post-actions button,
.comment-actions button {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #e0e0e0;
  background: white;
  border-radius: 6px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-detail {
  color: #667eea;
  border-color: #667eea !important;
}

.btn-edit {
  color: #48bb78;
}

.btn-delete,
.btn-unsave {
  color: #f56565;
}

.post-actions button:hover,
.comment-actions button:hover {
  background: #f5f5f5;
}

.post-title-ref {
  font-size: 0.9rem;
  color: #667eea;
  font-weight: 600;
}

.author {
  font-size: 0.85rem;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  color: #999;
}

.empty-state p {
  margin-bottom: 1rem;
  font-size: 1rem;
}

.btn-create {
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}
</style>
