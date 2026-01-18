<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { useBoard, type Comment, type Post } from "~/composables/useBoard";
import { useAuthStore } from "~/stores/auth"; // Assuming store exists

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
  <div class="page-container">
    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="post" class="content-wrapper">
      <!-- Post Header -->
      <div class="post-header">
        <span class="category">{{ post.category }}</span>
        <h1 class="title">{{ post.title }}</h1>
        <div class="meta">
          <span class="author">{{ post.author.displayName }}</span>
          <span class="date">{{
            new Date(post.createdAt).toLocaleString()
          }}</span>
          <span class="views">조회 {{ post.viewCount }}</span>
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
          {{ post.isLiked ? "❤️" : "🤍" }} 좋아요 {{ post.likeCount }}
        </button>
      </div>

      <hr class="divider" />

      <!-- Comments -->
      <div class="comments-section">
        <h3>댓글 {{ comments.length }}</h3>

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
            @keyup.enter="submitComment"
          />
          <button @click="submitComment">등록</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  min-height: 100vh;
}

.post-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
  margin-bottom: 20px;
}

.category {
  font-size: 0.9rem;
  color: #e94560;
  font-weight: bold;
}

.title {
  margin: 10px 0;
  font-size: 1.4rem;
  color: #333;
}

.meta {
  font-size: 0.8rem;
  color: #888;
  display: flex;
  gap: 15px;
}

.post-content {
  min-height: 200px;
  line-height: 1.8;
  color: #444;
  white-space: pre-wrap;
  margin-bottom: 40px;
}

.like-section {
  text-align: center;
  margin-bottom: 30px;
}

.like-btn {
  padding: 10px 20px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.2s;
}

.like-btn.liked {
  border-color: #e94560;
  color: #e94560;
  background: #fff0f3;
}

.divider {
  border: 0;
  border-top: 1px solid #eee;
  margin: 0 0 30px 0;
}

.comment-item {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f9f9f9;
}

.comment-header {
  margin-bottom: 5px;
}

.c-author {
  font-weight: bold;
  font-size: 0.9rem;
  margin-right: 10px;
}
.c-date {
  font-size: 0.8rem;
  color: #aaa;
}

.c-content {
  margin: 0;
  color: #555;
}

.comment-form {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.comment-form input {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.comment-form button {
  padding: 0 20px;
  background: #16213e;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
</style>
