<template>
  <div class="page-container fade-in">
    <div class="header">
      <div class="title-section">
        <h2 class="page-title">점주 소통방</h2>
        <p class="page-desc">사장님들의 자유로운 이야기 공간</p>
      </div>
      <button @click="goToWrite" class="write-btn hover-glow">
        <span class="icon">✏️</span> 글쓰기
      </button>
    </div>

    <!-- Glass Tabs -->
    <div class="filters-scroll">
      <div class="glass-pills">
        <button
          class="pill-btn"
          :class="{ active: category === undefined }"
          @click="setCategory(undefined)"
        >
          ♾️ 전체
        </button>
        <button
          class="pill-btn"
          :class="{ active: category === BoardCategory.NOTICE }"
          @click="setCategory(BoardCategory.NOTICE)"
        >
          📢 공지
        </button>
        <button
          class="pill-btn"
          :class="{ active: category === BoardCategory.FREE }"
          @click="setCategory(BoardCategory.FREE)"
        >
          🗣️ 자유
        </button>
        <button
          class="pill-btn"
          :class="{ active: category === BoardCategory.QA }"
          @click="setCategory(BoardCategory.QA)"
        >
          ❓ 질문
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>게시글을 불러오는 중...</p>
    </div>

    <div v-else class="post-list-container glass-panel fade-in delay-1">
      <div v-if="posts.length === 0" class="empty-state">
        <span class="icon">📭</span>
        <p>게시글이 없습니다.</p>
      </div>

      <div v-else class="post-list">
        <div
          v-for="post in posts"
          :key="post.id"
          class="post-item"
          @click="goToDetail(post.id)"
        >
          <div class="post-main">
            <span
              class="category-badge"
              :class="getCategoryClass(post.category)"
            >
              {{ getCategoryLabel(post.category) }}
            </span>
            <h3 class="post-title">{{ post.title }}</h3>
            <span v-if="post.imageUrls.length > 0" class="has-image">📷</span>
            <span v-if="post.commentCount > 0" class="comment-count"
              >[{{ post.commentCount }}]</span
            >
          </div>
          <div class="post-meta">
            <span class="author">👤 {{ post.author.displayName }}</span>
            <span class="divider">|</span>
            <span class="date">{{
              new Date(post.createdAt).toLocaleDateString()
            }}</span>
            <span class="divider">|</span>
            <span class="views">👀 {{ post.viewCount }}</span>
            <span class="likes">❤️ {{ post.likeCount }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

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

<style scoped lang="scss">
/* --- Variables --- */
$color-bg-dark: #121212;
$color-primary: #1e88e5;
$color-accent: #c5a059;
$color-danger: #e94560;
$glass-bg: rgba(255, 255, 255, 0.05);
$glass-border: rgba(255, 255, 255, 0.1);
$text-primary: #ffffff;
$text-secondary: #b0b0b0;

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
  padding-bottom: 80px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .title-section {
    .page-title {
      font-size: 1.5rem;
      color: $text-primary;
      margin: 0 0 4px 0;
    }
    .page-desc {
      color: $text-secondary;
      margin: 0;
      font-size: 0.95rem;
    }
  }

  .write-btn {
    background: linear-gradient(135deg, $color-accent, #d4a017);
    color: #1a1a2e;
    border: none;
    padding: 10px 18px;
    border-radius: 12px;
    font-weight: 800;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: transform 0.2s;
    box-shadow: 0 4px 15px rgba(197, 160, 89, 0.3);

    &:active {
      transform: scale(0.96);
    }
    .icon {
      font-size: 1.1rem;
    }
  }
}

/* Glass Pills (Tabs) */
.filters-scroll {
  overflow-x: auto;
  padding-bottom: 8px;
  margin-bottom: 20px;
  -ms-overflow-style: none;
  scrollbar-width: none;
  &::-webkit-scrollbar {
    display: none;
  }
}

.glass-pills {
  display: flex;
  gap: 10px;

  .pill-btn {
    padding: 8px 16px;
    border: 1px solid $glass-border;
    background: rgba(255, 255, 255, 0.05);
    color: $text-secondary;
    border-radius: 20px;
    cursor: pointer;
    white-space: nowrap;
    font-size: 0.95rem;
    transition: all 0.2s;

    &.active {
      background: rgba(197, 160, 89, 0.2);
      border-color: $color-accent;
      color: $color-accent;
      font-weight: bold;
    }
  }
}

/* Post List */
.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.post-item {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  cursor: pointer;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }
  &:hover {
    background: rgba(255, 255, 255, 0.03);
  }
}

.post-main {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;

  .post-title {
    margin: 0;
    font-size: 1.05rem;
    font-weight: 500;
    color: $text-primary;
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .has-image {
    font-size: 0.9rem;
  }

  .comment-count {
    color: $color-accent;
    font-size: 0.9rem;
    font-weight: bold;
  }
}

.category-badge {
  font-size: 0.75rem;
  padding: 3px 8px;
  border-radius: 6px;
  font-weight: bold;

  &.badge-notice {
    background: rgba(233, 69, 96, 0.15);
    color: #ff6b6b;
  }
  &.badge-free {
    background: rgba(30, 136, 229, 0.15);
    color: #64b5f6;
  }
  &.badge-qa {
    background: rgba(76, 175, 80, 0.15);
    color: #81c784;
  }
}

.post-meta {
  font-size: 0.85rem;
  color: #888;
  display: flex;
  align-items: center;
  gap: 8px;

  .divider {
    color: rgba(255, 255, 255, 0.1);
    font-size: 0.8rem;
  }
}

/* States */
.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: $text-secondary;
}
.empty-state {
  .icon {
    font-size: 3rem;
    display: block;
    margin-bottom: 10px;
    opacity: 0.5;
  }
}

.spinner {
  width: 30px;
  height: 30px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: $color-accent;
  border-radius: 50%;
  margin: 0 auto 16px;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.fade-in {
  animation: fadeIn 0.5s ease-out forwards;
}
.delay-1 {
  animation-delay: 0.1s;
}
</style>
