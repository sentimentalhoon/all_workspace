<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { useBoard, type Comment, type Post } from "~/composables/useBoard";
import { useAuthStore } from "~/stores/auth";

const { fetchPostById, fetchComments, createComment, toggleLike } = useBoard();
const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const post = ref<Post | null>(null);
const comments = ref<Comment[]>([]);
const newComment = ref("");
const loading = ref(true);

const postId = route.params.id as string;

const loadData = async () => {
  loading.value = true;
  try {
    const postRes = await fetchPostById(postId);
    post.value = postRes.data;
    const commentRes = await fetchComments(Number(postId));
    comments.value = commentRes.data;
  } catch (e) {
    alert("게시글을 불러오지 못했습니다.");
    router.back();
  } finally {
    loading.value = false;
  }
};

const handleLike = async () => {
  if (!post.value) return;
  try {
    const res = await toggleLike(post.value.id);
    post.value.isLiked = res.isLiked;
    post.value.likeCount += res.isLiked ? 1 : -1;
  } catch (e) {
    console.error(e);
  }
};

const submitComment = async () => {
  if (!newComment.value.trim()) return;

  try {
    await createComment(Number(postId), newComment.value);
    newComment.value = "";
    // Refresh comments
    const res = await fetchComments(Number(postId));
    comments.value = res.data;
  } catch (e) {
    alert("댓글 등록 실패");
  }
};

onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="page-container fade-in">
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>게시글을 불러오는 중...</p>
    </div>

    <div v-else-if="post" class="content-wrapper glass-panel">
      <!-- Post Header -->
      <div class="post-header">
        <span class="category-badge">{{ post.category }}</span>
        <h1 class="title">{{ post.title }}</h1>
        <div class="meta">
          <span class="author">👤 {{ post.author.displayName }}</span>
          <span class="divider">|</span>
          <span class="date">{{
            new Date(post.createdAt).toLocaleString()
          }}</span>
          <span class="divider">|</span>
          <span class="views">👀 {{ post.viewCount }}</span>
        </div>
      </div>

      <!-- Post Content -->
      <div class="post-content">
        {{ post.content }}
      </div>

      <!-- Like Button -->
      <div class="like-section">
        <button
          class="like-btn"
          :class="{ liked: post.isLiked }"
          @click="handleLike"
        >
          <span class="heart">{{ post.isLiked ? "❤️" : "🤍" }}</span>
          <span>좋아요 {{ post.likeCount }}</span>
        </button>
      </div>

      <div class="divider-line"></div>

      <!-- Comments -->
      <div class="comments-section">
        <h3 class="comments-title">댓글 {{ comments.length }}</h3>

        <div class="comment-list">
          <div
            v-for="comment in comments"
            :key="comment.id"
            class="comment-item"
          >
            <div class="comment-header">
              <span class="c-author">{{ comment.author.displayName }}</span>
              <span class="c-date">{{
                new Date(comment.createdAt).toLocaleDateString()
              }}</span>
            </div>
            <p class="c-content">{{ comment.content }}</p>
          </div>
        </div>

        <!-- Comment Form -->
        <div class="comment-form">
          <input
            v-model="newComment"
            placeholder="댓글을 남겨주세요."
            class="dark-input"
            @keyup.enter="submitComment"
          />
          <button @click="submitComment" class="send-btn">등록</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
/* --- Variables --- */
$color-bg-dark: #121212;
$color-primary: #1e88e5;
$color-secondary: #16213e; /* Added Navy */
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

.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

/* Header */
.post-header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 20px;
  margin-bottom: 24px;

  .category-badge {
    display: inline-block;
    padding: 4px 10px;
    background: rgba(255, 255, 255, 0.1);
    color: $color-accent;
    border-radius: 6px;
    font-size: 0.8rem;
    font-weight: bold;
    margin-bottom: 12px;
  }

  .title {
    margin: 0 0 12px 0;
    font-size: 1.5rem;
    color: $text-primary;
    line-height: 1.3;
  }

  .meta {
    font-size: 0.85rem;
    color: $text-secondary;
    display: flex;
    align-items: center;
    gap: 10px;

    .divider {
      color: rgba(255, 255, 255, 0.1);
      font-size: 0.7rem;
    }
  }
}

/* Content */
.post-content {
  min-height: 150px;
  line-height: 1.8;
  color: #ddd;
  white-space: pre-wrap;
  margin-bottom: 40px;
  font-size: 1.05rem;
}

/* Like Button */
.like-section {
  text-align: center;
  margin-bottom: 30px;

  .like-btn {
    padding: 10px 24px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(255, 255, 255, 0.05);
    color: $text-secondary;
    border-radius: 30px;
    cursor: pointer;
    font-size: 1rem;
    transition: all 0.3s;
    display: inline-flex;
    align-items: center;
    gap: 8px;

    &:hover {
      background: rgba(233, 69, 96, 0.1);
      border-color: $color-danger;
      color: $color-danger;
      transform: scale(1.05);
    }

    &.liked {
      background: rgba(233, 69, 96, 0.2);
      border-color: $color-danger;
      color: $color-danger;
      box-shadow: 0 0 15px rgba(233, 69, 96, 0.3);
    }

    .heart {
      font-size: 1.2rem;
    }
  }
}

.divider-line {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin-bottom: 30px;
}

/* Comments */
.comments-section {
  .comments-title {
    color: $text-primary;
    margin-bottom: 20px;
    font-size: 1.1rem;
    border-left: 3px solid $color-accent;
    padding-left: 10px;
  }

  .comment-item {
    padding: 16px;
    background: rgba(0, 0, 0, 0.2);
    border-radius: 12px;
    margin-bottom: 12px;
    border: 1px solid rgba(255, 255, 255, 0.03);

    .comment-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;
      font-size: 0.85rem;

      .c-author {
        font-weight: bold;
        color: $color-accent;
      }
      .c-date {
        color: #666;
      }
    }

    .c-content {
      margin: 0;
      color: #ccc;
      line-height: 1.5;
    }
  }

  .comment-form {
    display: flex;
    gap: 10px;
    margin-top: 24px;

    .dark-input {
      flex: 1;
      padding: 12px;
      background: rgba(0, 0, 0, 0.3);
      border: 1px solid $glass-border;
      border-radius: 10px;
      color: white;

      &:focus {
        outline: none;
        border-color: $color-accent;
      }
    }

    .send-btn {
      padding: 0 24px;
      background: $color-secondary; /* Navy */
      color: $color-accent; /* Gold */
      border: 1px solid $color-accent;
      border-radius: 10px;
      font-weight: bold;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: $color-accent;
        color: #000;
      }
    }
  }
}

/* Loading */
.loading-state {
  text-align: center;
  padding: 60px 0;
  color: $text-secondary;

  .spinner {
    width: 30px;
    height: 30px;
    border: 3px solid rgba(255, 255, 255, 0.1);
    border-top-color: $color-accent;
    border-radius: 50%;
    margin: 0 auto 16px;
    animation: spin 1s linear infinite;
  }
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
</style>
