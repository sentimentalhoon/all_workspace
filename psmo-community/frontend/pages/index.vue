<script setup lang="ts">
import type { BadUser } from "~/composables/useBlacklist";
import { useBlacklist } from "~/composables/useBlacklist";
import type { Product } from "~/composables/useMarket";
import { useMarket } from "~/composables/useMarket";

// Import Home Components
import HomeBadUserList from "~/components/home/HomeBadUserList.vue";
import HomeHero from "~/components/home/HomeHero.vue";
import HomeNoticeFeed from "~/components/home/HomeNoticeFeed.vue";
import HomePartnerBanners from "~/components/home/HomePartnerBanners.vue";
import HomeProductGrid from "~/components/home/HomeProductGrid.vue";
import HomeQuickMenu from "~/components/home/HomeQuickMenu.vue";

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
  <div class="home-container">
    <!-- Hero Section -->
    <HomeHero />

    <!-- Quick Access Navigation -->
    <HomeQuickMenu />

    <!-- Main Content Area: Split View -->
    <div class="content-grid">
      <!-- Recent Bad Users -->
      <HomeBadUserList :users="recentBadUsers" />

      <!-- Recent Products -->
      <HomeProductGrid :products="recentProducts" />

      <!-- Partner Banners -->
      <HomePartnerBanners />

      <!-- Notice Feed -->
      <HomeNoticeFeed />
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "~/assets/scss/variables" as *;

.home-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 40px;
  color: $text-primary;
  animation: fadeIn 0.6s ease-out;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 30px;
}
</style>
