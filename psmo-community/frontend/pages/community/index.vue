<script setup lang="ts">
import { useRouter } from "vue-router";
import { BoardCategory, useBoard, type Post } from "~/composables/useBoard";

const { fetchPosts } = useBoard();
const router = useRouter();

const posts = ref<Post[]>([]);
const loading = ref(true);
const page = ref(1);
const category = ref<BoardCategory | undefined>(undefined);

const loadData = async () => {
  loading.value = true;
  try {
    const res = await fetchPosts(page.value, 20, category.value);
    posts.value = res.data;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const setCategory = (cat?: BoardCategory) => {
  category.value = cat;
  page.value = 1;
  loadData();
};

const goToWrite = () => router.push("/community/write");
const goToDetail = (id: number) => router.push(`/community/${id}`);

const getCategoryLabel = (cat: BoardCategory) => {
  switch (cat) {
    case BoardCategory.NOTICE:
      return "공지";
    case BoardCategory.FREE:
      return "자유";
    case BoardCategory.QA:
      return "질문";
    default:
      return cat;
  }
};

const getCategoryClass = (cat: BoardCategory) => {
  switch (cat) {
    case BoardCategory.NOTICE:
      return "badge-notice";
    case BoardCategory.FREE:
      return "badge-free";
    case BoardCategory.QA:
      return "badge-qa";
    default:
      return "";
  }
};

onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="page-container">
    <div class="header">
      <div class="title-section">
        <h2>점주 소통방</h2>
        <p>사장님들의 자유로운 이야기 공간</p>
      </div>
      <button @click="goToWrite" class="write-btn">🖊️ 글쓰기</button>
    </div>

    <div class="tabs">
      <button
        :class="{ active: category === undefined }"
        @click="setCategory(undefined)"
      >
        전체
      </button>
      <button
        :class="{ active: category === BoardCategory.NOTICE }"
        @click="setCategory(BoardCategory.NOTICE)"
      >
        📢 공지
      </button>
      <button
        :class="{ active: category === BoardCategory.FREE }"
        @click="setCategory(BoardCategory.FREE)"
      >
        🗣️ 자유
      </button>
      <button
        :class="{ active: category === BoardCategory.QA }"
        @click="setCategory(BoardCategory.QA)"
      >
        ❓ 질문
      </button>
    </div>

    <div v-if="loading" class="loading">Loading...</div>

    <div v-else class="post-list">
      <div v-if="posts.length === 0" class="empty">게시글이 없습니다.</div>
      <div
        v-for="post in posts"
        :key="post.id"
        class="post-item"
        @click="goToDetail(post.id)"
      >
        <div class="post-main">
          <span class="category-badge" :class="getCategoryClass(post.category)">
            {{ getCategoryLabel(post.category) }}
          </span>
          <h3 class="post-title">{{ post.title }}</h3>
          <span v-if="post.imageUrls.length > 0" class="has-image">📷</span>
          <span v-if="post.commentCount > 0" class="comment-count"
            >[{{ post.commentCount }}]</span
          >
        </div>
        <div class="post-meta">
          <span class="author">{{ post.author.displayName }}</span>
          <span class="date">{{
            new Date(post.createdAt).toLocaleDateString()
          }}</span>
          <span class="views">조회 {{ post.viewCount }}</span>
          <span class="likes">❤️ {{ post.likeCount }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 15px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title-section h2 {
  margin: 0;
  color: #16213e;
}
.title-section p {
  margin: 5px 0 0;
  color: #888;
  font-size: 0.9rem;
}

.write-btn {
  background: #e94560;
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.tabs button {
  border: none;
  background: none;
  padding: 8px 16px;
  font-size: 1rem;
  color: #888;
  cursor: pointer;
  border-radius: 20px;
}

.tabs button.active {
  background: #16213e;
  color: #c5a059;
  font-weight: bold;
}

.post-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.post-item {
  padding: 15px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
}

.post-item:hover {
  background: #f9f9f9;
}

.post-main {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.post-title {
  margin: 0;
  font-size: 1rem;
  font-weight: normal;
  color: #333;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.category-badge {
  font-size: 0.8rem;
  padding: 2px 6px;
  border-radius: 4px;
  background: #eee;
  color: #555;
  white-space: nowrap;
}

.badge-notice {
  background: #ffebee;
  color: #c62828;
}
.badge-free {
  background: #e3f2fd;
  color: #1565c0;
}
.badge-qa {
  background: #e8f5e9;
  color: #2e7d32;
}

.comment-count {
  color: #e94560;
  font-size: 0.9rem;
  font-weight: bold;
}

.post-meta {
  font-size: 0.8rem;
  color: #999;
  display: flex;
  gap: 12px;
}

.empty {
  padding: 40px;
  text-align: center;
  color: #888;
}
</style>
