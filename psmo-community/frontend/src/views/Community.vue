<template>
  <div class="community">
    <h1>커뮤니티</h1>

    <div class="category-tabs">
      <button
        v-for="category in categories"
        :key="category"
        :class="{ active: selectedCategory === category }"
        @click="selectedCategory = category"
      >
        {{ category }}
      </button>
    </div>

    <div class="post-list">
      <div class="post-item" v-for="post in posts" :key="post.id">
        <div class="post-header">
          <span class="category-badge">{{ post.category }}</span>
          <span class="post-time">{{ post.time }}</span>
        </div>
        <h3>{{ post.title }}</h3>
        <p>{{ post.content }}</p>
        <div class="post-footer">
          <span>👤 {{ post.author }}</span>
          <span>💬 {{ post.comments }}</span>
          <span>👍 {{ post.likes }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const categories = ["전체", "자유게시판", "질문", "정보공유", "후기"];
const selectedCategory = ref("전체");

const posts = ref([
  {
    id: 1,
    category: "자유게시판",
    title: "첫 번째 게시글",
    content: "커뮤니티에 오신 것을 환영합니다!",
    author: "사용자1",
    comments: 5,
    likes: 12,
    time: "10분 전",
  },
  {
    id: 2,
    category: "질문",
    title: "질문이 있습니다",
    content: "이것은 테스트 게시글입니다.",
    author: "사용자2",
    comments: 3,
    likes: 7,
    time: "1시간 전",
  },
]);
</script>

<style scoped>
.community {
  max-width: 600px;
  margin: 0 auto;
}

h1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 1rem;
}

.category-tabs {
  display: flex;
  gap: 0.5rem;
  overflow-x: auto;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
}

.category-tabs button {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s;
}

.category-tabs button.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.category-tabs button:active {
  opacity: 0.8;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.post-item {
  background: white;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.post-item:active {
  transform: scale(0.98);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.category-badge {
  background: #667eea;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
}

.post-time {
  color: #999;
  font-size: 0.85rem;
}

.post-item h3 {
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.post-item p {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.75rem;
}

.post-footer {
  display: flex;
  gap: 1rem;
  color: #999;
  font-size: 0.85rem;
}
</style>
