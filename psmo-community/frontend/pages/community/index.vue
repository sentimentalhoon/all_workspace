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
          <button
            :class="{ active: currentTab === 'QA' }"
            @click="currentTab = 'QA'"
          >
            질문게시판
          </button>
        </div>

        <!-- Sub Categories -->
        <div v-if="availableSubCategories.length > 0" class="sub-category-tabs">
          <button
            :class="{ active: currentSubTab === undefined }"
            @click="currentSubTab = undefined"
          >
            전체
          </button>
          <button
            v-for="sub in availableSubCategories"
            :key="sub.value"
            :class="{ active: currentSubTab === sub.value }"
            @click="currentSubTab = sub.value"
          >
            {{ sub.label }}
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
              <span v-if="post.subCategory" class="sub-category-badge">
                {{ getSubCategoryLabel(post.subCategory) }}
              </span>
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
import { computed, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import BannerSection from "~/components/community/BannerSection.vue";
import {
  BoardCategory,
  BoardSubCategory,
  useBoard,
} from "~/composables/useBoard";

const router = useRouter();
const { fetchPosts } = useBoard();

const currentTab = ref("ALL");
const currentSubTab = ref<string | undefined>(undefined);
const posts = ref<any[]>([]);
const loading = ref(false);
const page = ref(1);
const size = 20;

const availableSubCategories = computed(() => {
  if (currentTab.value === BoardCategory.NOTICE) {
    return [
      { value: BoardSubCategory.MUST_READ, label: "필독" },
      { value: BoardSubCategory.UPDATE, label: "업데이트" },
      { value: BoardSubCategory.EVENT, label: "이벤트" },
    ];
  }
  if (currentTab.value === BoardCategory.FREE) {
    return [
      { value: BoardSubCategory.CHAT, label: "잡담" },
      { value: BoardSubCategory.HUMOR, label: "유머" },
      { value: BoardSubCategory.INFO, label: "정보" },
    ];
  }
  if (currentTab.value === BoardCategory.QA) {
    return [
      { value: BoardSubCategory.HARDWARE, label: "H/W" },
      { value: BoardSubCategory.SOFTWARE, label: "S/W" },
      { value: BoardSubCategory.OPERATION, label: "운영" },
      { value: BoardSubCategory.ETC, label: "기타" },
    ];
  }
  return [];
});

const loadPosts = async () => {
  loading.value = true;
  try {
    const category = currentTab.value === "ALL" ? undefined : currentTab.value;
    const res = await fetchPosts(
      page.value,
      size,
      category,
      currentSubTab.value,
    );
    posts.value = res.data;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

watch(currentTab, () => {
  page.value = 1;
  currentSubTab.value = undefined; // Reset sub-cat
  loadPosts();
});

watch(currentSubTab, () => {
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
  if (cat === "QA") return "질문";
  return cat;
};

const getSubCategoryLabel = (sub: string) => {
  const map: Record<string, string> = {
    MUST_READ: "필독",
    UPDATE: "업데이트",
    EVENT: "이벤트",
    CHAT: "잡담",
    HUMOR: "유머",
    INFO: "정보",
    HARDWARE: "H/W",
    SOFTWARE: "S/W",
    OPERATION: "운영",
    ETC: "기타",
  };
  return map[sub] || sub;
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
    /* Unifying to Mobile-style layout (Single column) */
  }

  .board-area {
    padding: 16px;
    min-height: 500px;

    .board-tabs {
      display: flex;
      gap: 12px;
      margin-bottom: 12px;
      padding-bottom: 12px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);

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

    .sub-category-tabs {
      display: flex;
      gap: 8px;
      margin-bottom: 16px;
      flex-wrap: wrap;

      button {
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid transparent;
        color: $text-secondary;
        font-size: 0.85rem;
        padding: 4px 12px;
        border-radius: 20px;
        cursor: pointer;
        transition: all 0.2s;

        &.active {
          background: rgba($color-primary, 0.2);
          color: $color-primary;
          border-color: rgba($color-primary, 0.3);
        }

        &:hover {
          background: rgba(255, 255, 255, 0.1);
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
            &.QA {
              color: #2196f3;
            }
          }

          .sub-category-badge {
            margin-left: 6px;
            color: $text-primary;
            background: rgba(255, 255, 255, 0.1);
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 0.75rem;
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
    order: -1; /* Always Top */

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
