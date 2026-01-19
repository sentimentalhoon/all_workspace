<template>
  <div class="community-page">
    <div class="page-header">
      <h2>점주 소통방</h2>
      <p class="subtitle">PC방 사장님들의 자유로운 이야기 공간</p>
    </div>

    <div class="content-grid">
      <!-- Main Board Area -->
      <div class="board-area glass-panel">
        <div class="board-tabs">
          <button
            :class="{ active: currentTab === 'ALL' }"
            @click="currentTab = 'ALL'"
          >
            전체
          </button>
          <button
            :class="{ active: currentTab === 'NOTICE' }"
            @click="currentTab = 'NOTICE'"
          >
            공지사항
          </button>
          <button
            :class="{ active: currentTab === 'FREE' }"
            @click="currentTab = 'FREE'"
          >
            자유게시판
          </button>
        </div>

        <div class="board-actions">
          <!-- Write Button -->
          <NuxtLink to="/community/write" class="write-btn">
            <span class="icon">✏️</span> 글쓰기
          </NuxtLink>
        </div>

        <div v-if="loading" class="loading">로딩 중...</div>
        <ul v-else class="post-list">
          <li
            v-for="post in posts"
            :key="post.id"
            class="post-item"
            @click="goToDetail(post.id)"
          >
            <div class="post-meta">
              <span class="category-badge" :class="post.category">{{
                getCategoryLabel(post.category)
              }}</span>
              <span class="date">{{ formatDate(post.createdAt) }}</span>
            </div>
            <div class="post-title">
              {{ post.title }}
              <span v-if="post.commentCount > 0" class="comment-count"
                >[{{ post.commentCount }}]</span
              >
            </div>
            <div class="post-info">
              <span>{{ post.author.displayName }}</span>
              <span>👀 {{ post.viewCount }}</span>
            </div>
          </li>
        </ul>

        <!-- Pagination (Simple Previous/Next for MVP) -->
        <div class="pagination">
          <button :disabled="page === 1" @click="changePage(-1)">이전</button>
          <span class="page-num">{{ page }}</span>
          <button :disabled="posts.length < size" @click="changePage(1)">
            다음
          </button>
        </div>
      </div>

      <!-- Sidebar / Widgets -->
      <div class="sidebar">
        <!-- Premium Affiliates -->
        <BannerSection />

        <!-- Telegram Link -->
        <a
          href="https://t.me/your_telegram_group_link"
          target="_blank"
          class="telegram-banner glass-panel"
        >
          <div class="tg-icon">✈️</div>
          <div class="tg-text">
            <strong>실시간 소통방 입장</strong>
            <span>텔레그램 지부 채널 바로가기</span>
          </div>
        </a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import BannerSection from "~/components/community/BannerSection.vue";
import { useBoard } from "~/composables/useBoard";

const router = useRouter();
const { fetchPosts } = useBoard();

const currentTab = ref("ALL");
const posts = ref<any[]>([]);
const loading = ref(false);
const page = ref(1);
const size = 20;

const loadPosts = async () => {
  loading.value = true;
  try {
    const category = currentTab.value === "ALL" ? undefined : currentTab.value;
    const res = await fetchPosts(page.value, size, category);
    posts.value = res.data;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

watch(currentTab, () => {
  page.value = 1;
  loadPosts();
});

const changePage = (delta: number) => {
  page.value += delta;
  loadPosts();
};

const goToDetail = (id: number) => {
  router.push(`/community/${id}`);
};

const getCategoryLabel = (cat: string) => {
  if (cat === "NOTICE") return "공지";
  if (cat === "FREE") return "자유";
  return cat;
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString();
};

onMounted(() => {
  loadPosts();
});
</script>

<style scoped lang="scss">
@use "~/assets/scss/variables" as *;
@use "~/assets/scss/main" as *;

.community-page {
  padding: 24px 16px;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 80px;

  .page-header {
    margin-bottom: 24px;
    h2 {
      font-size: 1.8rem;
      font-weight: 800;
      color: $text-primary;
      margin-bottom: 4px;
    }
    .subtitle {
      font-size: 0.9rem;
      color: $text-secondary;
    }
  }

  .content-grid {
    display: flex;
    flex-direction: column;
    gap: 24px;

    @media (min-width: 768px) {
      display: grid;
      grid-template-columns: 1fr 300px;
    }
  }

  .board-area {
    padding: 16px;
    min-height: 500px;

    .board-tabs {
      display: flex;
      gap: 12px;
      margin-bottom: 16px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      padding-bottom: 12px;

      button {
        background: none;
        border: none;
        color: $text-secondary;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        padding: 4px 8px;
        transition: color 0.2s;

        &.active {
          color: $color-primary;
          border-bottom: 2px solid $color-primary;
        }

        &:hover {
          color: $text-primary;
        }
      }
    }

    .board-actions {
      display: flex;
      justify-content: flex-end;
      margin-bottom: 12px;

      .write-btn {
        /* Copied glass-button styles manually */
        /* @include glass-button; */
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid $glass-border;
        color: $text-primary;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s;
        &:hover {
          background: rgba(255, 255, 255, 0.1);
        }

        font-size: 0.9rem;
        padding: 8px 16px;
        display: flex;
        align-items: center;
        gap: 6px;
      }
    }

    .post-list {
      list-style: none;
      padding: 0;
      margin: 0;

      .post-item {
        padding: 16px 8px;
        border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        cursor: pointer;
        transition: background 0.2s;

        &:hover {
          background: rgba(255, 255, 255, 0.02);
        }

        .post-meta {
          display: flex;
          justify-content: space-between;
          font-size: 0.8rem;
          color: $text-secondary;
          margin-bottom: 4px;

          .category-badge {
            &.NOTICE {
              color: $color-danger;
              font-weight: bold;
            }
            &.FREE {
              color: #4caf50;
            }
          }
        }

        .post-title {
          font-size: 1rem;
          color: $text-primary;
          margin-bottom: 4px;

          .comment-count {
            color: $color-primary;
            font-size: 0.8rem;
            margin-left: 4px;
          }
        }

        .post-info {
          font-size: 0.8rem;
          color: $text-secondary;
          display: flex;
          gap: 12px;
        }
      }
    }

    .pagination {
      display: flex;
      justify-content: center;
      gap: 16px;
      margin-top: 24px;
      align-items: center;

      button {
        background: rgba(255, 255, 255, 0.1);
        border: none;
        color: $text-primary;
        padding: 6px 12px;
        border-radius: 4px;
        &:disabled {
          opacity: 0.5;
        }
      }
      .page-num {
        color: $color-primary;
        font-weight: bold;
      }
    }
  }

  .sidebar {
    display: flex;
    flex-direction: column;
    gap: 24px;
    order: -1; /* Mobile: Top */

    @media (min-width: 768px) {
      order: 0; /* Desktop: Right (Natural Order) */
    }

    .telegram-banner {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      text-decoration: none;
      transition: transform 0.2s;
      border: 1px solid rgba($color-primary, 0.3);
      background: linear-gradient(
        135deg,
        rgba($color-primary, 0.1),
        transparent
      );

      &:hover {
        transform: translateY(-2px);
        border-color: $color-primary;
      }

      .tg-icon {
        font-size: 2rem;
      }

      .tg-text {
        display: flex;
        flex-direction: column;

        strong {
          color: $color-primary;
          font-size: 1.1rem;
        }
        span {
          color: $text-secondary;
          font-size: 0.8rem;
        }
      }
    }
  }
}
</style>
