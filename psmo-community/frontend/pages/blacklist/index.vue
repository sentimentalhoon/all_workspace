<template>
  <div class="page-container">
    <div class="header-section">
      <h2 class="page-title">🚨 불량 사용자 블랙리스트</h2>
      <p class="page-desc">피해 사례를 공유하고 매장을 보호하세요.</p>
    </div>

    <!-- Search Box -->
    <div class="search-section glass-panel">
      <div class="search-box">
        <input
          v-model="searchKeyword"
          placeholder="이름 (익명) 또는 키워드로 검색"
          class="dark-input"
          @keyup.enter="handleSearch"
        />
        <button
          class="search-btn"
          @click="handleSearch"
          :disabled="searchLoading"
        >
          <span v-if="searchLoading" class="spinner-sm"></span>
          <span v-else>🔍 조회</span>
        </button>
      </div>

      <button class="write-btn" @click="router.push('/blacklist/create')">
        📝 피해 사례 등록
      </button>
    </div>

    <div class="results-area">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
      </div>

      <div
        v-else-if="searched && searchResults.length === 0"
        class="no-results glass-panel"
      >
        <div class="icon">🤔</div>
        <p>검색 결과가 없습니다.</p>
        <p class="sub">
          등록된 불량 사용자가 아니거나, 정보가 일치하지 않습니다.
        </p>
      </div>

      <div v-else class="result-list">
        <div
          v-for="item in searchResults"
          :key="item.id"
          class="bad-user-card glass-panel hover-glow"
          @click="router.push(`/blacklist/${item.id}`)"
        >
          <div class="card-header">
            <div class="user-main">
              <span class="bad-badge">주의</span>
              <span class="region">📍 {{ item.region }}</span>
            </div>
            <span class="date">{{
              new Date(item.createdAt).toLocaleDateString()
            }}</span>
          </div>

          <div class="card-body">
            <p class="reason text-truncate">“ {{ item.reason }} ”</p>

            <div
              v-if="item.images && item.images.length > 0"
              class="image-preview"
            >
              <!-- Show only first image thumbnail -->
              <img :src="item.images[0].thumbnailUrl" alt="Thumbnail" />
              <div v-if="item.images.length > 1" class="image-count">
                +{{ item.images.length - 1 }}
              </div>
            </div>
          </div>

          <div class="card-footer">
            <span class="reporter">제보자: {{ item.reporterName }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { searchBadUsers } = useBlacklist();
const router = useRouter();

definePageMeta({
  auth: false,
});

// --- Search Logic ---
const searchKeyword = ref("");
const searchResults = ref<any[]>([]);
const searchLoading = ref(false);
const searched = ref(false);
const loading = ref(true);

onMounted(async () => {
  try {
    searchResults.value = await searchBadUsers("");
    searched.value = true;
  } catch (e) {
    console.error("블랙리스트 로드 실패:", e);
  } finally {
    loading.value = false;
  }
});

const handleSearch = async () => {
  searchLoading.value = true;
  searched.value = true;
  try {
    searchResults.value = await searchBadUsers(searchKeyword.value);
  } catch (e) {
    alert("검색 중 오류가 발생했습니다.");
  } finally {
    searchLoading.value = false;
  }
};
</script>

<style scoped lang="scss">
/* --- Theme Variables --- */
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
  padding-bottom: 40px;
  animation: fadeIn 0.5s ease;
}

.header-section {
  text-align: center;
  margin-bottom: 30px;

  .page-title {
    color: $text-primary;
    margin: 0 0 8px 0;
    font-size: 1.5rem;
  }

  .page-desc {
    color: $text-secondary;
    font-size: 0.95rem;
    margin: 0;
  }
}

.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.search-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.search-box {
  display: flex;
  gap: 12px;

  .dark-input {
    flex: 1;
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid $glass-border;
    color: white;
    padding: 14px;
    border-radius: 10px;
    font-size: 1rem;

    &:focus {
      outline: none;
      border-color: $color-primary;
      box-shadow: 0 0 0 2px rgba(30, 136, 229, 0.2);
    }
  }

  .search-btn {
    padding: 0 24px;
    background: $color-primary;
    color: white;
    border: none;
    border-radius: 10px;
    font-weight: bold;
    cursor: pointer;
    transition: background 0.2s;
    min-width: 80px;

    &:hover {
      background: lighten($color-primary, 10%);
    }
    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
  }
}

.write-btn {
  width: 100%;
  padding: 14px;
  background: $color-danger;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: lighten($color-danger, 5%);
    transform: translateY(-2px);
  }
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.bad-user-card {
  cursor: pointer;
  transition:
    transform 0.2s,
    border-color 0.2s;

  &:hover {
    transform: translateY(-2px);
    border-color: $color-danger;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 12px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
    margin-bottom: 12px;

    .user-main {
      display: flex;
      align-items: center;
      gap: 10px;

      .bad-badge {
        background: rgba(233, 69, 96, 0.2);
        color: $color-danger;
        font-size: 0.75rem;
        padding: 4px 8px;
        border-radius: 4px;
        font-weight: bold;
      }

      .region {
        font-weight: bold;
        font-size: 1.1rem;
      }
    }

    .date {
      font-size: 0.8rem;
      color: #666;
    }
  }

  .card-body {
    display: flex;
    justify-content: space-between;
    gap: 16px;

    .reason {
      flex: 1;
      color: #ddd;
      line-height: 1.6;
      margin: 0;
      font-style: italic;
      border-left: 2px solid $color-danger;
      padding-left: 10px;

      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .image-preview {
      position: relative;
      width: 80px;
      height: 80px;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 8px;
        border: 1px solid rgba(255, 255, 255, 0.1);
      }

      .image-count {
        position: absolute;
        bottom: 0px;
        right: 0px;
        background: rgba(0, 0, 0, 0.7);
        color: white;
        font-size: 0.7rem;
        padding: 2px 6px;
        border-top-left-radius: 6px;
        border-bottom-right-radius: 6px;
      }
    }
  }

  .card-footer {
    text-align: right;
    font-size: 0.8rem;
    color: #666;
    margin-top: 12px;
  }
}

.no-results {
  text-align: center;
  padding: 60px 20px;
  color: $text-secondary;

  .icon {
    font-size: 3rem;
    margin-bottom: 16px;
  }
  p {
    margin: 0;
    font-size: 1.1rem;
  }
  .sub {
    font-size: 0.9rem;
    margin-top: 8px;
    opacity: 0.7;
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

.spinner-sm {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  display: inline-block;
}

@keyframes spin {
  100% {
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
