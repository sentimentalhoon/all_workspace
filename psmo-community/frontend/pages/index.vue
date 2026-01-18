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

// 시간대에 따른 인사말 생성
const greetingMessage = computed(() => {
  const hour = new Date().getHours();
  if (hour < 12) return "활기찬 오전 되세요!";
  if (hour < 18) return "오후도 힘내세요!";
  return "오늘 하루도 고생 많으셨습니다.";
});

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
    <!-- Hero Section: User Status & Welcome -->
    <section class="hero-section glass-panel">
      <div class="hero-content">
        <div class="greeting-box">
          <span class="sub-greeting">{{ greetingMessage }}</span>
          <h1 class="username">
            <span class="highlight">{{
              authStore.user?.displayName || "Guest"
            }}</span>
            사장님
          </h1>
        </div>
        <div class="stats-box">
          <div class="stat-item">
            <span class="label">보유 포인트</span>
            <strong class="value gold-text"
              >{{ authStore.user?.score?.toLocaleString() || 0 }} P</strong
            >
          </div>
        </div>
      </div>
      <div class="hero-bg-decoration"></div>
    </section>

    <!-- Quick Access Navigation -->
    <nav class="quick-menu">
      <NuxtLink
        to="/blacklist"
        class="menu-card glass-panel"
        title="불량 사용자 검색"
      >
        <div class="icon-box red-glow">🚫</div>
        <span class="menu-title">블랙리스트</span>
        <span class="menu-desc">불량 이용자 조회</span>
      </NuxtLink>
      <NuxtLink to="/market" class="menu-card glass-panel" title="매장 거래">
        <div class="icon-box blue-glow">🤝</div>
        <span class="menu-title">매장 거래</span>
        <span class="menu-desc">PC방 양도/양수</span>
      </NuxtLink>
      <NuxtLink
        to="/market?category=PC_FULL"
        class="menu-card glass-panel"
        title="중고 장터"
      >
        <div class="icon-box gold-glow">📦</div>
        <span class="menu-title">중고 장터</span>
        <span class="menu-desc">PC 부품/집기</span>
      </NuxtLink>
      <NuxtLink to="/community" class="menu-card glass-panel" title="커뮤니티">
        <div class="icon-box green-glow">💬</div>
        <span class="menu-title">소통방</span>
        <span class="menu-desc">운영 노하우</span>
      </NuxtLink>
    </nav>

    <!-- Main Content Area: Split View -->
    <div class="content-grid">
      <!-- Recent Bad Users -->
      <section class="content-section" v-if="recentBadUsers.length > 0">
        <div class="section-header">
          <h3>🚨 최근 불량 사용자</h3>
          <NuxtLink to="/blacklist" class="more-btn">더보기</NuxtLink>
        </div>
        <div class="list-container glass-panel">
          <div
            v-for="user in recentBadUsers"
            :key="user.id"
            class="list-item hover-glow"
            @click="navigateTo(`/blacklist/${user.id}`)"
          >
            <div class="user-avatar-wrapper">
              <div
                v-if="user.images && user.images.length > 0"
                class="user-avatar"
              >
                <img :src="user.images[0].thumbnailUrl" alt="thumb" />
              </div>
              <div v-else class="user-avatar placeholder">
                <span>📍</span>
              </div>
            </div>

            <div class="info-col">
              <div class="top-row">
                <span class="region-badge">📍 {{ user.region }}</span>
                <span class="date">{{
                  new Date(user.createdAt).toLocaleDateString()
                }}</span>
              </div>
              <div class="reason">“{{ user.reason }}”</div>
            </div>

            <div class="arrow-icon">›</div>
          </div>
        </div>
      </section>

      <!-- Recent Products -->
      <section class="content-section" v-if="recentProducts.length > 0">
        <div class="section-header">
          <h3>🛒 최근 올라온 매물</h3>
          <NuxtLink to="/market" class="more-btn">더보기</NuxtLink>
        </div>
        <div class="product-grid">
          <div
            v-for="product in recentProducts"
            :key="product.id"
            class="product-card glass-panel hover-lift"
            @click="navigateTo(`/market/${product.id}`)"
          >
            <div class="product-thumb">
              <!-- Placeholder Icon used if no image -->
              <div
                v-if="!product.images || product.images.length === 0"
                class="no-image-placeholder"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                  <circle cx="8.5" cy="8.5" r="1.5"></circle>
                  <polyline points="21 15 16 10 5 21"></polyline>
                </svg>
              </div>
              <img
                v-else
                :src="product.images?.[0]?.thumbnailUrl"
                alt="상품 이미지"
                loading="lazy"
              />
              <span
                class="status-badge"
                :class="product.status.toLowerCase()"
                >{{ product.status }}</span
              >
            </div>
            <div class="product-details">
              <h4 class="title">{{ product.title }}</h4>
              <div class="price-row">
                <span class="price"
                  >{{ product.price.toLocaleString() }}원</span
                >
                <span class="location">{{
                  product.realEstate?.locationCity || "지역"
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped lang="scss">
/* --- Variables & Theme --- */
$color-bg-dark: #121212;
$color-primary: #1e88e5; /* Blue */
$color-accent: #c5a059; /* Gold */
$color-danger: #e94560; /* Red */
$glass-bg: rgba(255, 255, 255, 0.05);
$glass-border: rgba(255, 255, 255, 0.1);
$glass-blur: 10px;
$text-primary: #ffffff;
$text-secondary: #b0b0b0;

.home-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 40px;
  color: $text-primary;
  animation: fadeIn 0.6s ease-out;
}

/* --- Common Utility Classes --- */
.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur($glass-blur);
  -webkit-backdrop-filter: blur($glass-blur);
  border: 1px solid $glass-border;
  border-radius: 16px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
}

.gold-text {
  color: $color-accent;
}
.red-glow {
  background: rgba(233, 69, 96, 0.2);
  color: #ff6b6b;
}
.blue-glow {
  background: rgba(30, 136, 229, 0.2);
  color: #64b5f6;
}
.gold-glow {
  background: rgba(197, 160, 89, 0.2);
  color: #ffd54f;
}
.green-glow {
  background: rgba(76, 175, 80, 0.2);
  color: #81c784;
}

/* --- Hero Section --- */
.hero-section {
  position: relative;
  padding: 30px;
  overflow: hidden;
  background: linear-gradient(
    135deg,
    rgba(22, 33, 62, 0.8) 0%,
    rgba(15, 52, 96, 0.8) 100%
  );

  .hero-content {
    position: relative;
    z-index: 2;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .sub-greeting {
    display: block;
    font-size: 0.9rem;
    color: $text-secondary;
    margin-bottom: 4px;
  }

  h1.username {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 300;

    .highlight {
      font-weight: 700;
      color: $text-primary;
    }
  }

  .stats-box {
    text-align: right;

    .stat-item {
      display: flex;
      flex-direction: column;
    }

    .label {
      font-size: 0.8rem;
      color: $text-secondary;
    }
    .value {
      font-size: 1.5rem;
      font-weight: 700;
    }
  }

  .hero-bg-decoration {
    position: absolute;
    top: -50%;
    right: -10%;
    width: 300px;
    height: 300px;
    background: radial-gradient(
      circle,
      rgba(197, 160, 89, 0.15) 0%,
      transparent 70%
    );
    border-radius: 50%;
    z-index: 1;
    pointer-events: none;
  }
}

/* --- Quick Menu --- */
.quick-menu {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;

  .menu-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    text-decoration: none;
    transition:
      transform 0.2s,
      background 0.2s;

    &:hover {
      transform: translateY(-5px);
      background: rgba(255, 255, 255, 0.1);
    }

    .icon-box {
      width: 50px;
      height: 50px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
      margin-bottom: 12px;
    }

    .menu-title {
      font-weight: 700;
      font-size: 1rem;
      color: $text-primary;
      margin-bottom: 4px;
    }

    .menu-desc {
      font-size: 0.75rem;
      color: $text-secondary;
    }
  }
}

/* --- Content Grid --- */
.content-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 30px;
}

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

/* --- List Styling (Bad Users) --- */
.list-container {
  padding: 12px;
}

.list-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  gap: 16px;
  cursor: pointer;
  border-radius: 12px;
  transition:
    background 0.2s,
    transform 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.03);
  }

  &.hover-glow:hover {
    box-shadow: 0 0 15px rgba(233, 69, 96, 0.1);
    border-color: rgba(233, 69, 96, 0.3);
    transform: translateX(4px);
  }

  &:last-child {
    border-bottom: none;
  }

  .user-avatar-wrapper {
    position: relative;
  }

  .user-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: #222;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 1.2rem;
    overflow: hidden;
    border: 2px solid rgba(255, 255, 255, 0.1);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    &.placeholder {
      background: linear-gradient(135deg, #333 0%, #444 100%);
    }
  }

  .info-col {
    flex: 1;
    min-width: 0;

    .top-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 6px;
    }

    .region-badge {
      font-weight: 700;
      font-size: 0.95rem;
      color: white;
    }

    .date {
      font-size: 0.8rem;
      color: $text-secondary;
    }

    .reason {
      font-size: 0.9rem;
      color: #ff6b6b; /* Red text for danger/alert feel */
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      font-style: italic;
      opacity: 0.9;
    }
  }

  .arrow-icon {
    color: $text-secondary;
    font-size: 1.2rem;
    opacity: 0.5;
  }
}

/* --- Product Grid --- */
.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.product-card {
  overflow: hidden;
  cursor: pointer;

  .product-thumb {
    position: relative;
    width: 100%;
    aspect-ratio: 4 / 3;
    background: #2a2a2a;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .no-image-placeholder {
      color: #555;
    }

    .status-badge {
      position: absolute;
      top: 8px;
      left: 8px;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 0.7rem;
      font-weight: bold;

      &.pending {
        background: rgba(255, 193, 7, 0.9);
        color: black;
      }
      &.sale {
        background: rgba(30, 136, 229, 0.9);
        color: white;
      }
      &.reserved {
        background: rgba(255, 87, 34, 0.9);
        color: white;
      }
      &.sold {
        background: rgba(158, 158, 158, 0.9);
        color: white;
      }
    }
  }

  .product-details {
    padding: 12px;

    .title {
      font-size: 0.95rem;
      font-weight: 600;
      margin: 0 0 6px 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      color: $text-primary;
    }

    .price-row {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .price {
        font-weight: 700;
        color: $color-accent;
        font-size: 1rem;
      }
      .location {
        font-size: 0.75rem;
        color: $text-secondary;
      }
    }
  }
}

/* --- Mobile Responsiveness --- */
@media (max-width: 600px) {
  .quick-menu {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;

    .stats-box {
      width: 100%;
      text-align: left;
      border-top: 1px solid rgba(255, 255, 255, 0.1);
      padding-top: 12px;
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
