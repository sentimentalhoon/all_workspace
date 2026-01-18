<script setup lang="ts">
import type { BadUser } from "~/composables/useBlacklist";
import { useBlacklist } from "~/composables/useBlacklist";
import type { Product } from "~/composables/useMarket";
import { useMarket } from "~/composables/useMarket";

/**
 * 홈(대시보드) 페이지입니다.
 * 내 포인트(Score)를 보여주고, 주요 메뉴로 이동하는 버튼들을 보여줍니다.
 * 최신 불량 사용자 및 매물 정보를 요약해서 보여줍니다.
 */
const authStore = useAuthStore();
const { searchBadUsers } = useBlacklist();
const { fetchProducts } = useMarket();

const recentBadUsers = ref<BadUser[]>([]);
const recentProducts = ref<Product[]>([]);

// 데이터 로딩
onMounted(async () => {
  try {
    // 1. 최근 불량 사용자 5명
    const badUsers = await searchBadUsers();
    recentBadUsers.value = badUsers.slice(0, 5);

    // 2. 최근 매물 5개
    const productsResponse = await fetchProducts(1, 4); // 4개만 가져옴 (그리드 맞춤)
    if (productsResponse && productsResponse.data) {
      recentProducts.value = productsResponse.data;
    }
  } catch (e) {
    console.error("Failed to load dashboard data", e);
  }
});
</script>

<template>
  <div class="home-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <h2>안전한 매장 운영의 시작</h2>
        <p>불량 사용자 공유 및 매장 거래 플랫폼</p>
        <button class="primary-btn" @click="navigateTo('/blacklist')">
          🚨 불량 사용자 조회
        </button>
      </div>
    </div>

    <!-- Quick Menu Grid -->
    <div class="menu-grid">
      <NuxtLink to="/blacklist" class="menu-item highlight">
        <span class="icon">🚫</span>
        <span class="text">블랙리스트</span>
        <span class="sub-text">불량 이용자 조회</span>
      </NuxtLink>
      <NuxtLink to="/market" class="menu-item">
        <span class="icon">🤝</span>
        <span class="text">매장 거래</span>
        <span class="sub-text">PC방 양도/양수</span>
      </NuxtLink>
      <NuxtLink to="/market?category=PC_FULL" class="menu-item">
        <span class="icon">📦</span>
        <span class="text">중고 장터</span>
        <span class="sub-text">PC 부품/집기</span>
      </NuxtLink>
      <NuxtLink to="/community" class="menu-item">
        <span class="icon">💬</span>
        <span class="text">점주 소통방</span>
        <span class="sub-text">운영 노하우 공유</span>
      </NuxtLink>
    </div>

    <!-- Dashboard Status -->
    <div class="dashboard-card">
      <div class="user-info">
        <span class="greeting">사장님, 안녕하세요!</span>
        <strong class="username">{{
          authStore.user?.displayName || "Guest"
        }}</strong>
      </div>
      <div class="points">
        <span>보유 포인트</span>
        <strong>{{ authStore.user?.score?.toLocaleString() || 0 }} P</strong>
      </div>
    </div>

    <!-- Recent Bad Users Section -->
    <section class="dashboard-section" v-if="recentBadUsers.length > 0">
      <div class="section-header">
        <h3>최근 등록된 불량 사용자</h3>
        <NuxtLink to="/blacklist" class="more-link">더보기 ></NuxtLink>
      </div>
      <div class="bad-user-list">
        <div
          v-for="user in recentBadUsers"
          :key="user.id"
          class="bad-user-card"
        >
          <div class="bad-user-name">
            {{ user.name }} <span class="phone">({{ user.phoneLast4 }})</span>
          </div>
          <div class="bad-user-reason">{{ user.reason }}</div>
          <div class="bad-user-date">
            {{ new Date(user.createdAt).toLocaleDateString() }}
          </div>
        </div>
      </div>
    </section>

    <!-- Recent Products Section -->
    <section class="dashboard-section" v-if="recentProducts.length > 0">
      <div class="section-header">
        <h3>최근 올라온 매물</h3>
        <NuxtLink to="/market" class="more-link">더보기 ></NuxtLink>
      </div>
      <div class="product-grid">
        <div
          v-for="product in recentProducts"
          :key="product.id"
          class="product-card"
          @click="navigateTo(`/market/${product.id}`)"
        >
          <div class="product-info">
            <span class="badge" :class="product.status.toLowerCase()">{{
              product.status
            }}</span>
            <div class="product-title">{{ product.title }}</div>
            <div class="product-price">
              {{ product.price.toLocaleString() }}원
            </div>
            <div class="product-meta">
              {{ product.realEstate?.locationCity || product.category }}
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.hero-section {
  background: linear-gradient(135deg, #16213e 0%, #0f3460 100%);
  padding: 30px 20px;
  border-radius: 16px;
  color: white;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.hero-content h2 {
  margin: 0 0 10px 0;
  font-size: 1.4rem;
  font-weight: 700;
}

.hero-content p {
  margin: 0 0 20px 0;
  opacity: 0.8;
  font-size: 0.9rem;
}

.primary-btn {
  background: #e94560; /* Red Accent */
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  width: 100%;
  max-width: 200px;
  box-shadow: 0 4px 6px rgba(233, 69, 96, 0.3);
  transition: transform 0.2s;
}

.primary-btn:active {
  transform: scale(0.98);
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.menu-item {
  background: white;
  padding: 20px 15px;
  border-radius: 12px;
  text-decoration: none;
  color: #333;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  border: 1px solid transparent;
}

.menu-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.menu-item.highlight {
  border: 1px solid #e94560;
  background: #fff5f6;
}

.menu-item .icon {
  font-size: 2rem;
  margin-bottom: 5px;
}

.menu-item .text {
  font-weight: 700;
  font-size: 1rem;
  color: #16213e;
}

.menu-item .sub-text {
  font-size: 0.75rem;
  color: #888;
}

.dashboard-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  border-left: 5px solid #c5a059; /* Gold border */
}

.user-info {
  display: flex;
  flex-direction: column;
}

.greeting {
  font-size: 0.8rem;
  color: #666;
}

.username {
  font-size: 1rem;
  color: #333;
}

.points {
  text-align: right;
  display: flex;
  flex-direction: column;
}

.points span {
  font-size: 0.7rem;
  color: #888;
}

.points strong {
  font-size: 1.1rem;
  color: #c5a059;
}

/* Dashboard Section Styles */
.dashboard-section {
  margin-top: 10px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #16213e;
}

.more-link {
  font-size: 0.8rem;
  color: #888;
  text-decoration: none;
}

.bad-user-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.bad-user-card {
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  border-left: 3px solid #e94560;
}

.bad-user-name {
  font-weight: bold;
  font-size: 0.95rem;
}

.bad-user-name .phone {
  font-size: 0.8rem;
  color: #666;
  font-weight: normal;
}

.bad-user-reason {
  margin-top: 5px;
  font-size: 0.9rem;
  color: #555;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bad-user-date {
  margin-top: 5px;
  font-size: 0.75rem;
  color: #999;
  text-align: right;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

.product-info {
  padding: 12px;
}

.product-title {
  font-size: 0.9rem;
  font-weight: bold;
  margin: 5px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price {
  font-size: 0.95rem;
  color: #16213e;
  font-weight: bold;
}

.product-meta {
  font-size: 0.75rem;
  color: #888;
  margin-top: 4px;
}

.badge {
  font-size: 0.7rem;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: bold;
  text-transform: uppercase;
}

.badge.sale {
  background: #e3f2fd;
  color: #1976d2;
}

.badge.reserved {
  background: #fff3e0;
  color: #f57c00;
}

.badge.sold {
  background: #eeeeee;
  color: #9e9e9e;
}
</style>
