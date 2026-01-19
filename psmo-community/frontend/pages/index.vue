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
                <img :src="user.images[0]?.thumbnailUrl" alt="thumb" />
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

      <!-- Affiliate Banners -->
      <section class="content-section">
        <div class="section-header">
          <h3>🤝 제휴 업체</h3>
        </div>
        <div class="banner-scroll-container">
          <div class="banner-card glass-panel" title="PC방 먹거리 납품">
            <div class="banner-icon">🍔</div>
            <div class="banner-info">
              <span class="banner-title">푸드 딜리버리</span>
              <span class="banner-desc">전국 최저가 식자재 납품</span>
            </div>
          </div>
          <div class="banner-card glass-panel" title="PC 유지보수">
            <div class="banner-icon">💻</div>
            <div class="banner-info">
              <span class="banner-title">컴닥터 24시</span>
              <span class="banner-desc">야간 긴급 출동 서비스</span>
            </div>
          </div>
          <div class="banner-card glass-panel" title="인테리어 리모델링">
            <div class="banner-icon">🎨</div>
            <div class="banner-info">
              <span class="banner-title">공간 디자인</span>
              <span class="banner-desc">프리미엄 인테리어 시공</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Notices -->
      <section class="content-section">
        <div class="section-header">
          <h3>📢 공지사항</h3>
          <NuxtLink to="/community" class="more-btn">더보기</NuxtLink>
        </div>
        <div class="notice-list glass-panel">
          <div class="notice-item" @click="navigateTo('/community')">
            <span class="notice-tag important">필독</span>
            <span class="notice-title">성피천국 커뮤니티 이용 수칙 안내</span>
            <span class="notice-date">4.20</span>
          </div>
          <div class="notice-item" @click="navigateTo('/community')">
            <span class="notice-tag event">이벤트</span>
            <span class="notice-title"
              >[오픈기념] 포인트 2배 적립 이벤트 진행!</span
            >
            <span class="notice-date">4.18</span>
          </div>
          <div class="notice-item" @click="navigateTo('/community')">
            <span class="notice-tag">점검</span>
            <span class="notice-title"
              >서버 안정화 작업 안내 (02:00 ~ 04:00)</span
            >
            <span class="notice-date">4.15</span>
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
    border-radius: 12px;
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
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 150px;
    }

    .date {
      font-size: 0.8rem;
      color: $text-secondary;
      flex-shrink: 0;
      margin-left: 8px;
    }

    .reason {
      font-size: 0.9rem;
      color: #ff6b6b;

      /* Multi-line truncation (max 1 line or 2 lines) */
      display: -webkit-box;
      -webkit-line-clamp: 1;
      line-clamp: 1; /* Standard property */
      -webkit-box-orient: vertical;
      overflow: hidden;
      white-space: normal; /* Override nowrap */

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

    /* --- Premium Partner Banners --- */
    .partner-slider {
      display: flex;
      gap: 16px;
      overflow-x: auto;
      padding: 4px;
      padding-bottom: 12px;
      scroll-snap-type: x mandatory;
      -webkit-overflow-scrolling: touch;

      &::-webkit-scrollbar {
        height: 0; /* Hide scrollbar completely for cleaner look */
      }
    }

    .partner-card {
      position: relative;
      min-width: 260px;
      height: 140px;
      border-radius: 20px;
      overflow: hidden;
      cursor: pointer;
      scroll-snap-align: center;
      transition: transform 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);

      &:hover {
        transform: translateY(-4px) scale(1.02);
      }

      /* Background Gradients */
      &.food-theme .card-bg {
        background: linear-gradient(135deg, #ff9966 0%, #ff5e62 100%);
      }
      &.tech-theme .card-bg {
        background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%);
      }
      &.design-theme .card-bg {
        background: linear-gradient(135deg, #4568dc 0%, #b06ab3 100%);
      }

      .card-bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        opacity: 0.8;
        transition: opacity 0.3s;
      }
      &:hover .card-bg {
        opacity: 1;
      }

      .card-content {
        position: relative;
        z-index: 2;
        padding: 20px;
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        background: rgba(0, 0, 0, 0.2); /* Soft overlay */
        backdrop-filter: blur(2px);
      }

      .partner-badge {
        align-self: flex-start;
        padding: 4px 10px;
        border-radius: 20px;
        background: rgba(255, 255, 255, 0.25);
        color: white;
        font-size: 0.7rem;
        font-weight: 800;
        backdrop-filter: blur(5px);
        border: 1px solid rgba(255, 255, 255, 0.3);
      }

      .partner-icon {
        position: absolute;
        top: 20px;
        right: 20px;
        font-size: 2.5rem;
        opacity: 0.8;
        filter: drop-shadow(0 4px 10px rgba(0, 0, 0, 0.3));
      }

      .partner-text {
        h4 {
          margin: 0;
          font-size: 1.1rem;
          color: white;
          font-weight: 700;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        }
        p {
          margin: 4px 0 0;
          font-size: 0.85rem;
          color: rgba(255, 255, 255, 0.9);
        }
      }
    }

    /* --- Modern Notice Feed --- */
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

    /* Ad Label */
    .ad-label {
      font-size: 0.7rem;
      color: rgba(255, 255, 255, 0.4);
      border: 1px solid rgba(255, 255, 255, 0.2);
      padding: 2px 6px;
      border-radius: 4px;
      margin-left: auto;
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
  .hero-section {
    padding: 20px;

    .hero-content {
      flex-direction: row; /* Keep row for compact height */
      align-items: center;
      gap: 12px;

      .greeting-box {
        .sub-greeting {
          font-size: 0.8rem;
          margin-bottom: 2px;
        }
        h1.username {
          font-size: 1.2rem;
        }
      }

      .stats-box {
        width: auto;
        text-align: right;
        border-top: none;
        padding-top: 0;

        .label {
          font-size: 0.7rem;
        }
        .value {
          font-size: 1.2rem;
        }
      }
    }
  }

  .quick-menu {
    grid-template-columns: repeat(2, 1fr);
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
