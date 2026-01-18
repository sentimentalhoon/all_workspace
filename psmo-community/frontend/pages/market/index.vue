<script setup lang="ts">
import { useMarket, type Product } from "~/composables/useMarket";

const { fetchProducts } = useMarket();
const router = useRouter();

// State
const products = ref<Product[]>([]);
const loading = ref(true);
const page = ref(1);
const route = useRoute();
const categoryFilter = ref<string | undefined>(
  (route.query.category as string) || "PC_BUSINESS",
);

const loadData = async () => {
  loading.value = true;
  try {
    const res = await fetchProducts(page.value, 20, categoryFilter.value);
    products.value = res.data;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

// Watch for route query changes (for deep linking)
watch(
  () => route.query.category,
  (newCat) => {
    if (newCat) {
      categoryFilter.value = newCat as string;
    } else {
      // If query removed, maybe default? Or stay?
      // Let's stick to current logic: if query empty, maybe user wants "ALL" or "Default".
      // If clicking nav, query might be empty.
    }
    page.value = 1;
    loadData();
  },
);

const setCategory = (cat?: string) => {
  categoryFilter.value = cat;
  page.value = 1;
  loadData();
};

const goToCreate = () => router.push("/market/create");
const goToDetail = (id: number) => router.push(`/market/${id}`);

onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="page-container">
    <div class="header-section fade-in">
      <div class="title-group">
        <h2 class="page-title">매장 거래 / 중고 장터</h2>
        <p class="page-desc">검증된 매물을 안전하게 거래하세요.</p>
      </div>
      <button @click="goToCreate" class="create-btn hover-glow">
        <span class="icon">✏️</span> 글쓰기
      </button>
    </div>

    <!-- Glass Filter Pills -->
    <div class="filters-scroll fade-in delay-1">
      <div class="glass-pills">
        <button
          class="pill-btn"
          :class="{ active: categoryFilter === 'PC_BUSINESS' }"
          @click="setCategory('PC_BUSINESS')"
        >
          🤝 매장 매매
        </button>
        <button
          class="pill-btn"
          :class="{ active: categoryFilter === undefined }"
          @click="setCategory(undefined)"
        >
          ♾️ 전체 보기
        </button>
        <button
          class="pill-btn"
          :class="{
            active: categoryFilter === 'PC_FULL' || categoryFilter === 'ETC',
          }"
          @click="setCategory('PC_FULL')"
        >
          📦 부품/기타
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>매물 정보를 불러오는 중입니다...</p>
    </div>

    <div v-else class="product-grid fade-in delay-2">
      <div
        v-for="item in products"
        :key="item.id"
        class="product-card glass-panel"
        @click="goToDetail(item.id)"
      >
        <div class="img-wrapper">
          <img
            v-if="item.images[0]?.url"
            :src="item.images[0].url"
            class="thumbnail"
            loading="lazy"
          />
          <div v-else class="thumbnail no-image">
            <span class="placeholder-icon">📷</span>
          </div>

          <div
            v-if="item.status !== 'SALE'"
            class="status-badge"
            :class="item.status"
          >
            {{
              item.status === "PENDING"
                ? "승인 대기"
                : item.status === "SOLD"
                  ? "판매 완료"
                  : item.status === "RESERVED"
                    ? "예약중"
                    : item.status
            }}
          </div>
        </div>

        <div class="content">
          <h3 class="title">{{ item.title }}</h3>

          <div
            v-if="item.category === 'PC_BUSINESS' && item.realEstate"
            class="store-info"
          >
            <div class="info-row">
              <span class="label">보증금</span>
              <span class="value">{{
                item.realEstate.deposit.toLocaleString()
              }}</span>
            </div>
            <div class="info-row">
              <span class="label">월세</span>
              <span class="value">{{
                item.realEstate.monthlyRent.toLocaleString()
              }}</span>
            </div>
            <div class="info-row highlight">
              <span class="label">권리금</span>
              <span class="value gold-text">{{
                item.price.toLocaleString()
              }}</span>
            </div>
            <div class="location-tag">
              📍 {{ item.realEstate.locationCity }}
              {{ item.realEstate.locationDistrict }}
            </div>
          </div>

          <div v-else class="parts-info">
            <div class="price-box">
              <span class="currency">₩</span>
              <span class="price">{{ item.price.toLocaleString() }}</span>
            </div>
            <span class="cat-tag">{{ item.category }}</span>
          </div>

          <div class="meta">
            <span>👀 {{ item.viewCount }}</span>
            <span>{{ new Date(item.createdAt).toLocaleDateString() }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

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
  padding: 10px;
  max-width: 1000px;
  margin: 0 auto;
  padding-bottom: 40px;
}

/* Header */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .title-group {
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

  .create-btn {
    background: linear-gradient(135deg, $color-accent 0%, #e6b32e 100%);
    color: #1a1a2e;
    border: none;
    padding: 12px 20px;
    border-radius: 12px;
    font-weight: 800;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    box-shadow: 0 4px 15px rgba(197, 160, 89, 0.3);
    transition: transform 0.2s;

    &:active {
      transform: scale(0.96);
    }
    .icon {
      font-size: 1.1rem;
    }
  }
}

/* Filters */
.filters-scroll {
  overflow-x: auto;
  padding-bottom: 8px;
  margin-bottom: 24px;

  /* Hide scrollbar */
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
    padding: 10px 20px;
    border: 1px solid $glass-border;
    background: rgba(255, 255, 255, 0.05);
    color: $text-secondary;
    border-radius: 24px;
    cursor: pointer;
    white-space: nowrap;
    transition: all 0.2s;
    font-size: 0.95rem;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
      color: white;
    }

    &.active {
      background: rgba(197, 160, 89, 0.2);
      border-color: $color-accent;
      color: $color-accent;
      font-weight: bold;
      box-shadow: 0 0 15px rgba(197, 160, 89, 0.1);
    }
  }
}

/* Grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.product-card {
  overflow: hidden;
  cursor: pointer;
  transition:
    transform 0.3s,
    box-shadow 0.3s;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.3);
    border-color: rgba(255, 255, 255, 0.2);
  }
}

.img-wrapper {
  position: relative;
  height: 180px;
  background: #1e1e1e;
  overflow: hidden;

  .thumbnail {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s;
  }

  /* Image Zoom Effect */
  &:hover .thumbnail {
    transform: scale(1.05);
  }

  .no-image {
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.03);

    .placeholder-icon {
      font-size: 2.5rem;
      opacity: 0.5;
    }
  }

  .status-badge {
    position: absolute;
    top: 10px;
    right: 10px;
    padding: 6px 12px;
    border-radius: 8px;
    font-size: 0.75rem;
    font-weight: 800;
    backdrop-filter: blur(4px);

    &.PENDING {
      background: rgba(255, 193, 7, 0.8);
      color: #000;
    }
    &.SOLD {
      background: rgba(158, 158, 158, 0.8);
      color: #fff;
    }
    &.RESERVED {
      background: rgba(255, 87, 34, 0.8);
      color: #fff;
    }
  }
}

.content {
  padding: 16px;

  .title {
    margin: 0 0 12px;
    font-size: 1.1rem;
    color: $text-primary;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  /* Store Info Style */
  .store-info {
    font-size: 0.9rem;

    .info-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 6px;
      color: $text-secondary;

      .value {
        color: white;
      }
    }

    .highlight {
      margin-top: 8px;
      padding-top: 8px;
      border-top: 1px solid rgba(255, 255, 255, 0.1);

      .label {
        color: $text-primary;
      }
      .gold-text {
        color: $color-accent;
        font-weight: bold;
        font-size: 1.1rem;
      }
    }

    .location-tag {
      margin-top: 8px;
      font-size: 0.8rem;
      color: #888;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  /* Parts Info Style */
  .parts-info {
    .price-box {
      color: $color-accent;
      margin-bottom: 6px;

      .currency {
        font-size: 0.9rem;
        margin-right: 2px;
      }
      .price {
        font-size: 1.4rem;
        font-weight: 800;
      }
    }

    .cat-tag {
      display: inline-block;
      font-size: 0.75rem;
      background: rgba(255, 255, 255, 0.05);
      padding: 4px 8px;
      border-radius: 4px;
      color: $text-secondary;
    }
  }

  .meta {
    display: flex;
    justify-content: space-between;
    margin-top: 16px;
    font-size: 0.75rem;
    color: #666;
    padding-top: 12px;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
  }
}

/* Animations */
.fade-in {
  animation: fadeIn 0.6s ease-out forwards;
  opacity: 0;
}
.delay-1 {
  animation-delay: 0.1s;
}
.delay-2 {
  animation-delay: 0.2s;
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

@media (max-width: 600px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
