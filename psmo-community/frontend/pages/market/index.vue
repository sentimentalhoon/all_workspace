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
    <div class="header">
      <div class="title-section">
        <h2>매장 거래 / 중고 장터</h2>
        <p>검증된 매물만 거래하세요</p>
      </div>
      <button @click="goToCreate" class="create-btn">+ 글쓰기</button>
    </div>

    <div class="filters">
      <button
        :class="{ active: categoryFilter === 'PC_BUSINESS' }"
        @click="setCategory('PC_BUSINESS')"
      >
        🤝 매장 매매
      </button>
      <button
        :class="{ active: categoryFilter === undefined }"
        @click="setCategory(undefined)"
      >
        ♾️ 전체 보기
      </button>
      <button
        :class="{
          active: categoryFilter === 'PC_FULL' || categoryFilter === 'ETC',
        }"
        @click="setCategory('PC_FULL')"
      >
        📦 부품/기타
      </button>
    </div>

    <div v-if="loading" class="loading">Loading...</div>

    <div v-else class="product-grid">
      <div
        v-for="item in products"
        :key="item.id"
        class="product-card"
        @click="goToDetail(item.id)"
      >
        <div class="img-wrapper">
          <img
            :src="
              item.images[0]?.url ||
              'https://via.placeholder.com/300x200?text=No+Image'
            "
            class="thumbnail"
          />
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
              <span class="value">{{ item.price.toLocaleString() }}</span>
            </div>
            <div class="location">
              {{ item.realEstate.locationCity }}
              {{ item.realEstate.locationDistrict }}
            </div>
          </div>

          <div v-else class="parts-info">
            <p class="price">{{ item.price.toLocaleString() }}원</p>
            <p class="cat">{{ item.category }}</p>
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

<style scoped>
.page-container {
  padding: 10px;
  max-width: 1000px;
  margin: 0 auto;
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

.create-btn {
  background: #e94560;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

.filters {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.filters button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  white-space: nowrap;
}

.filters button.active {
  background: #16213e;
  color: #c5a059;
  border-color: #16213e;
  font-weight: bold;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.img-wrapper {
  position: relative;
  height: 180px;
  background: #eee;
}

.thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 0.8rem;
  font-weight: bold;
  color: white;
  background: rgba(0, 0, 0, 0.6);
}

.status-badge.PENDING {
  background: #ffc107;
  color: #000;
}

.status-badge.SOLD {
  background: #6c757d;
  color: #fff;
}

.status-badge.RESERVED {
  background: #17a2b8;
  color: #fff;
}

.content {
  padding: 15px;
}

.title {
  margin: 0 0 10px;
  font-size: 1.1rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.store-info {
  font-size: 0.9rem;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.info-row.highlight {
  color: #e94560;
  font-weight: bold;
  margin-top: 5px;
  font-size: 1rem;
}

.location {
  margin-top: 8px;
  font-size: 0.8rem;
  color: #888;
}

.parts-info .price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #16213e;
  margin: 0;
}

.parts-info .cat {
  font-size: 0.8rem;
  color: #888;
}

.meta {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  font-size: 0.8rem;
  color: #aaa;
  border-top: 1px solid #f0f0f0;
  padding-top: 10px;
}

@media (max-width: 600px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
