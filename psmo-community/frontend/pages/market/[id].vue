<script setup lang="ts">
import { useRoute } from "vue-router";
import { useMarket, type Product } from "~/composables/useMarket";

const route = useRoute();
const { fetchProductById } = useMarket();

const product = ref<Product | null>(null);
const loading = ref(true);
const activeImageIndex = ref(0);

onMounted(async () => {
  const id = route.params.id as string;
  try {
    const res = await fetchProductById(id);
    product.value = res.data;
  } catch (e) {
    alert("상품을 불러오지 못했습니다.");
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div v-if="loading" class="loading">Loading...</div>
  <div v-else-if="!product" class="error">존재하지 않는 상품입니다.</div>

  <div v-else class="page-container">
    <!-- Image Gallery -->
    <div class="gallery-section">
      <div class="main-image">
        <img
          :src="
            product.images[activeImageIndex]?.url ||
            'https://via.placeholder.com/600x400?text=No+Image'
          "
          alt="Main Product Image"
        />
      </div>
      <div v-if="product.images.length > 1" class="thumbnails">
        <div
          v-for="(img, idx) in product.images"
          :key="img.id"
          class="thumb"
          :class="{ active: idx === activeImageIndex }"
          @click="activeImageIndex = idx"
        >
          <img :src="img.url" />
        </div>
      </div>
    </div>

    <!-- Product Info -->
    <div class="info-section">
      <div class="header">
        <span class="category-badge">{{
          product.category === "PC_BUSINESS" ? "매장 매매" : product.category
        }}</span>
        <h1 class="title">{{ product.title }}</h1>
        <div class="meta">
          <span>작성자: {{ product.seller.displayName }}</span>
          <span>{{ new Date(product.createdAt).toLocaleDateString() }}</span>
          <span>조회 {{ product.viewCount }}</span>
        </div>
      </div>

      <div class="price-box">
        <span class="label">{{
          product.category === "PC_BUSINESS" ? "권리금" : "판매 가격"
        }}</span>
        <span class="value">{{ product.price.toLocaleString() }}원</span>
      </div>

      <!-- Real Estate Details -->
      <div
        v-if="product.category === 'PC_BUSINESS' && product.realEstate"
        class="detail-grid"
      >
        <div class="grid-item">
          <span class="label">보증금</span>
          <span class="val"
            >{{ product.realEstate.deposit.toLocaleString() }}원</span
          >
        </div>
        <div class="grid-item">
          <span class="label">월세</span>
          <span class="val"
            >{{ product.realEstate.monthlyRent.toLocaleString() }}원</span
          >
        </div>
        <div class="grid-item">
          <span class="label">관리비</span>
          <span class="val"
            >{{ product.realEstate.managementFee.toLocaleString() }}원</span
          >
        </div>
        <div class="grid-item">
          <span class="label">PC 대수</span>
          <span class="val">{{ product.realEstate.pcCount }}대</span>
        </div>
        <div class="grid-item">
          <span class="label">평균 월매출</span>
          <span class="val"
            >{{
              product.realEstate.averageMonthlyRevenue.toLocaleString()
            }}원</span
          >
        </div>
        <div class="grid-item">
          <span class="label">층수 / 면적</span>
          <span class="val"
            >{{ product.realEstate.floor }}층 /
            {{ product.realEstate.areaMeters }}㎡</span
          >
        </div>
        <div class="grid-item full-width">
          <span class="label">위치</span>
          <span class="val"
            >{{ product.realEstate.locationCity }}
            {{ product.realEstate.locationDistrict }}</span
          >
        </div>
      </div>

      <div class="description">
        <h3>상세 내용</h3>
        <p>{{ product.description }}</p>
      </div>

      <div class="action-bar">
        <!-- Call/Chat buttons placeholders -->
        <button class="chat-btn">💬 채팅하기</button>
        <button class="call-btn">📞 전화하기</button>
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

.gallery-section {
  margin-bottom: 30px;
}

.main-image {
  width: 100%;
  height: 400px;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-image img {
  max-width: 100%;
  max-height: 100%;
}

.thumbnails {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  overflow-x: auto;
}

.thumb {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
}

.thumb.active {
  border-color: #e94560;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header {
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
  margin-bottom: 20px;
}

.category-badge {
  background: #f0f0f0;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #555;
}

.title {
  margin: 10px 0;
  font-size: 1.5rem;
  color: #333;
}

.meta {
  color: #888;
  font-size: 0.9rem;
  display: flex;
  gap: 15px;
}

.price-box {
  margin-bottom: 30px;
}

.price-box .value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #e94560;
  display: block;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  background: #f9f9f9;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 30px;
}

.grid-item {
  display: flex;
  flex-direction: column;
}

.grid-item.full-width {
  grid-column: span 2;
}

.grid-item .label {
  font-size: 0.8rem;
  color: #888;
}

.grid-item .val {
  font-weight: bold;
  color: #333;
}

.description h3 {
  margin-bottom: 15px;
}

.description p {
  line-height: 1.6;
  color: #444;
  white-space: pre-line;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px;
  background: white;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
  justify-content: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.action-bar button {
  flex: 1;
  max-width: 300px;
  padding: 15px;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
}

.chat-btn {
  background: #eee;
  color: #333;
}

.call-btn {
  background: #16213e;
  color: #c5a059;
}

@media (min-width: 800px) {
  .page-container {
    margin-top: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>
