<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import {
  BoardCategory,
  BoardSubCategory,
  useBoard,
  type Post,
} from "~/composables/useBoard";

const router = useRouter();
const { fetchPosts } = useBoard();
const notices = ref<Post[]>([]);

onMounted(async () => {
  try {
    const res = await fetchPosts(1, 5, BoardCategory.NOTICE);
    notices.value = res.data;
  } catch (e) {
    console.error("Failed to load notices", e);
  }
});

const navigateTo = (path: string) => {
  router.push(path);
};

const goToDetail = (id: number) => {
  router.push(`/community/${id}`);
};

const getNoticeStyle = (sub?: BoardSubCategory) => {
  switch (sub) {
    case BoardSubCategory.MUST_READ:
      return "important";
    case BoardSubCategory.EVENT:
      return "event";
    case BoardSubCategory.UPDATE:
      return "system";
    default:
      return "system";
  }
};

const getNoticeLabel = (sub?: BoardSubCategory) => {
  const map: Record<string, string> = {
    MUST_READ: "필독",
    UPDATE: "업데이트",
    EVENT: "이벤트",
  };
  return map[sub as string] || "공지";
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}.${date.getDate()}`;
};
</script>

<template>
  <section class="content-section">
    <div class="section-header">
      <h3>📢 성피 뉴스</h3>
      <NuxtLink to="/community" class="more-btn">전체보기</NuxtLink>
    </div>
    <div class="notice-feed glass-panel">
      <!-- Loading State -->
      <div
        v-if="notices.length === 0"
        class="feed-item"
        style="justify-content: center; color: #888"
      >
        등록된 공지사항이 없습니다.
      </div>

      <!-- Feed Items -->
      <div
        v-for="notice in notices"
        :key="notice.id"
        class="feed-item"
        @click="goToDetail(notice.id)"
      >
        <div class="feed-icon" :class="getNoticeStyle(notice.subCategory)">
          <!-- Icon: Important (Alert) -->
          <svg
            v-if="notice.subCategory === BoardSubCategory.MUST_READ"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path
              d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"
            ></path>
            <line x1="12" y1="9" x2="12" y2="13"></line>
            <line x1="12" y1="17" x2="12.01" y2="17"></line>
          </svg>

          <!-- Icon: Event (Gift) -->
          <svg
            v-else-if="notice.subCategory === BoardSubCategory.EVENT"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path>
            <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path>
            <path d="M4 22h16"></path>
            <path
              d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"
            ></path>
            <path
              d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"
            ></path>
            <path d="M18 2H6v7a6 6 0 0 0 12 0V2z"></path>
          </svg>

          <!-- Icon: System/Update (Cog) -->
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <circle cx="12" cy="12" r="3"></circle>
            <path
              d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"
            ></path>
          </svg>
        </div>
        <div class="feed-content">
          <span class="feed-title">{{ notice.title }}</span>
          <span class="feed-meta"
            >{{ getNoticeLabel(notice.subCategory) }} ·
            {{ formatDate(notice.createdAt) }}</span
          >
        </div>
        <div class="feed-arrow">›</div>
      </div>
    </div>
  </section>
</template>

<style scoped lang="scss">
@use "~/assets/scss/variables" as *;

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;

  h3 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 700;
    color: $text-primary;
  }

  .more-btn {
    font-size: 0.85rem;
    color: $text-secondary;
    text-decoration: none;
    transition: color 0.2s;
    &:hover {
      color: $color-primary;
    }
  }
}

.notice-feed {
  padding: 0;
  overflow: hidden;
}

.feed-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  cursor: pointer;
  position: relative;
  transition: background 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.05);
  }
  &:active {
    background: rgba(255, 255, 255, 0.08);
  }

  &:last-child {
    border-bottom: none;
  }

  .feed-icon {
    width: 40px;
    height: 40px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    flex-shrink: 0;

    &.important {
      background: rgba(233, 69, 96, 0.15);
      color: #ff6b6b;
    }
    &.event {
      background: rgba(197, 160, 89, 0.15);
      color: #ffd54f;
    }
    &.system {
      background: rgba(158, 158, 158, 0.15);
      color: #b0b0b0;
    }
  }

  .feed-content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 4px;

    .feed-title {
      font-size: 0.95rem;
      color: $text-primary;
      font-weight: 500;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .feed-meta {
      font-size: 0.8rem;
      color: $text-secondary;
    }
  }

  .feed-arrow {
    color: rgba(255, 255, 255, 0.2);
    font-size: 1.2rem;
    margin-left: 12px;
    transition:
      transform 0.2s,
      color 0.2s;
  }

  &:hover .feed-arrow {
    transform: translateX(3px);
    color: rgba(255, 255, 255, 0.6);
  }
}
</style>
