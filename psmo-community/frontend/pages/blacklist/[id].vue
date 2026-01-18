<script setup lang="ts">
import { useBlacklist, type BadUser } from "~/composables/useBlacklist";

const { fetchBadUserById, deleteBadUser } = useBlacklist();
const route = useRoute();
const router = useRouter();
const { user } = storeToRefs(useAuthStore());

const reportId = Number(route.params.id);
const item = ref<BadUser | null>(null);
const loading = ref(true);

const activeImageIndex = ref(0);

onMounted(async () => {
  try {
    item.value = await fetchBadUserById(reportId);
  } catch (e) {
    alert("게시글을 불러올 수 없습니다.");
    router.push("/blacklist");
  } finally {
    loading.value = false;
  }
});

const isOwnerOrAdmin = computed(() => {
  if (!item.value || !user.value) return false;
  return item.value.reporterId === user.value.id || user.value.role === "ADMIN";
});

const handleDelete = async () => {
  if (!confirm("정말로 삭제하시겠습니까? 복구할 수 없습니다.")) return;
  try {
    await deleteBadUser(reportId);
    alert("삭제되었습니다.");
    router.push("/blacklist");
  } catch (e: any) {
    alert("삭제 실패: " + (e.message || e));
  }
};

const handleEdit = () => {
  router.push(`/blacklist/create?id=${reportId}`);
};

const setActiveImage = (index: number) => {
  activeImageIndex.value = index;
};
</script>

<template>
  <div class="page-container fade-in">
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
    </div>

    <div v-else-if="item" class="content-wrapper">
      <!-- Header -->
      <div class="header glass-panel">
        <div class="top-row">
          <span class="bad-badge">주의</span>
          <div class="meta">
            <span class="date">{{
              new Date(item.createdAt).toLocaleDateString()
            }}</span>
          </div>
        </div>
        <h1 class="region-title">📍 {{ item.region }}</h1>
        <div class="reporter-info">제보자: {{ item.reporterName }}</div>
      </div>

      <!-- Logic to Display Images if any -->
      <div
        v-if="item.images && item.images.length > 0"
        class="gallery-section glass-panel"
      >
        <div class="main-image-frame">
          <img :src="item.images[activeImageIndex].url" class="main-img" />
        </div>
        <div class="thumbnails" v-if="item.images.length > 1">
          <div
            v-for="(img, idx) in item.images"
            :key="img.id"
            class="thumb-wrapper"
            :class="{ active: idx === activeImageIndex }"
            @click="setActiveImage(idx)"
          >
            <img :src="img.thumbnailUrl" />
          </div>
        </div>
      </div>

      <!-- Details -->
      <div class="details-section glass-panel">
        <div class="detail-row" v-if="item.incidentDate">
          <label>피해 발생일</label>
          <span>{{ item.incidentDate }}</span>
        </div>
        <div class="detail-row" v-if="item.physicalDescription">
          <label>신체 특징</label>
          <span>{{ item.physicalDescription }}</span>
        </div>

        <hr class="divider" />

        <div class="reason-content">
          <h3>피해 내용</h3>
          <p>{{ item.reason }}</p>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="actions" v-if="isOwnerOrAdmin">
        <button class="edit-btn glass-btn" @click="handleEdit">수정</button>
        <button class="delete-btn glass-btn" @click="handleDelete">삭제</button>
      </div>

      <div class="nav-actions">
        <button class="back-btn" @click="router.push('/blacklist')">
          목록으로
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
$color-danger: #e94560;
$glass-bg: rgba(255, 255, 255, 0.05);
$glass-border: rgba(255, 255, 255, 0.1);
$text-secondary: #b0b0b0;

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 16px;
}

.header {
  .top-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .bad-badge {
    background: rgba(233, 69, 96, 0.2);
    color: $color-danger;
    padding: 4px 8px;
    border-radius: 4px;
    font-weight: bold;
    font-size: 0.8rem;
  }

  .date {
    color: #666;
    font-size: 0.9rem;
  }

  .region-title {
    margin: 0 0 10px 0;
    font-size: 1.8rem;
    color: white;
  }

  .reporter-info {
    color: $text-secondary;
    font-size: 0.95rem;
  }
}

.gallery-section {
  .main-image-frame {
    width: 100%;
    max-height: 500px;
    border-radius: 12px;
    overflow: hidden;
    margin-bottom: 12px;
    background: black;
    display: flex;
    justify-content: center;

    .main-img {
      max-width: 100%;
      max-height: 500px;
      object-fit: contain;
    }
  }

  .thumbnails {
    display: flex;
    gap: 8px;
    overflow-x: auto;
    padding-bottom: 4px;

    .thumb-wrapper {
      width: 70px;
      height: 70px;
      flex-shrink: 0;
      border-radius: 8px;
      overflow: hidden;
      border: 2px solid transparent;
      cursor: pointer;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      &.active {
        border-color: $color-danger;
      }
    }
  }
}

.details-section {
  .detail-row {
    display: flex;
    margin-bottom: 8px;

    label {
      width: 100px;
      color: $text-secondary;
      font-weight: 500;
    }
  }

  .divider {
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    border-bottom: none;
    margin: 20px 0;
  }

  .reason-content {
    h3 {
      color: $color-danger;
      font-size: 1.1rem;
      margin-bottom: 12px;
    }
    p {
      line-height: 1.6;
      color: #ddd;
      white-space: pre-wrap;
    }
  }
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-bottom: 24px;

  .glass-btn {
    padding: 10px 20px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    background: rgba(255, 255, 255, 0.05);
    color: white;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
  }

  .edit-btn {
  }
  .delete-btn {
    color: $color-danger;
    border-color: rgba(233, 69, 96, 0.3);
  }
  .delete-btn:hover {
    background: rgba(233, 69, 96, 0.1);
  }
}

.nav-actions {
  text-align: center;
  .back-btn {
    background: transparent;
    border: none;
    color: $text-secondary;
    cursor: pointer;
    text-decoration: underline;
    font-size: 0.9rem;
  }
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 60px;

  .spinner {
    width: 40px;
    height: 40px;
    border: 4px solid rgba(255, 255, 255, 0.1);
    border-top-color: $color-danger;
    border-radius: 50%;
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
  }
  to {
    opacity: 1;
  }
}
</style>
