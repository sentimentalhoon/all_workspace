<script setup lang="ts">
/**
 * 기본 레이아웃 (Smart Header 적용)
 * route에 따라 헤더의 내용(뒤로가기, 제목, 액션 등)이 동적으로 변경됩니다.
 */
const route = useRoute();
const router = useRouter();

// 하단 네비게이션 표시 여부 (로그인, 관리자, 그리고 상세/작성 페이지에서는 숨김)
const showBottomNav = computed(() => {
  const path = route.path;
  const hideOnPaths = ["/login", "/admin"];
  if (hideOnPaths.some((p) => path.startsWith(p))) return false;

  // 서브 페이지(상세, 글쓰기 등)에서는 하단 탭바 숨기기 (Native App 스타일)
  // 예: /market (보임), /market/create (숨김), /market/123 (숨김)
  // 예: /community (보임), /community/write (숨김), /community/123 (숨김)
  // 단, /my 는 항상 보임
  if (path === "/market" || path === "/community") return true;
  if (path.startsWith("/market/") || path.startsWith("/community/"))
    return false;

  return true;
});

// --- Smart Header Logic ---

const pageTitle = computed(() => {
  const path = route.path;
  if (path === "/") return "성피천국";
  if (path.startsWith("/market/create")) return "매물 등록";
  if (path.startsWith("/market")) return "매장 거래";
  if (path.startsWith("/community/write")) return "글쓰기";
  if (path.startsWith("/community")) return "점주 소통방";
  if (path.startsWith("/blacklist")) return "블랙리스트";
  if (path.startsWith("/my")) return "워라벨 페이지";
  return "성피천국";
});

const isHome = computed(() => route.path === "/");
const canGoBack = computed(() => route.path !== "/" && route.path !== "/login");

const handleTitleClick = () => {
  const path = route.path;
  if (path.startsWith("/market")) {
    router.push("/market");
  } else if (path.startsWith("/community")) {
    router.push("/community");
  } else if (path.startsWith("/blacklist")) {
    router.push("/blacklist");
  } else if (path.startsWith("/my")) {
    router.push("/my");
  } else {
    router.push("/");
  }
};

const goBack = () => {
  router.back();
};
</script>

<template>
  <div class="mobile-container">
    <!-- Smart Header -->
    <header class="smart-header glass-panel-header">
      <!-- Left: Back Button or Logo -->
      <div class="header-left">
        <button
          v-if="canGoBack"
          @click="goBack"
          class="icon-btn back-btn"
          aria-label="뒤로가기"
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
            <path d="M15 18l-6-6 6-6" />
          </svg>
        </button>
        <span v-else class="brand-logo">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="#e94560"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path
              d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
            />
          </svg>
        </span>
      </div>

      <!-- Center: Title -->
      <div class="header-center">
        <transition name="fade-slide" mode="out-in">
          <h1
            :key="pageTitle"
            @click="handleTitleClick"
            class="clickable-title"
          >
            {{ pageTitle }}
          </h1>
        </transition>
      </div>

      <!-- Right: Actions -->
      <div class="header-right">
        <NuxtLink to="/my" class="icon-btn profile-btn" aria-label="내 정보">
          <svg
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
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
            <path d="M13.73 21a2 2 0 0 1-3.46 0" />
          </svg>
        </NuxtLink>
      </div>
    </header>

    <!-- Main Content -->
    <main class="content-wrapper" :class="{ 'with-bottom-nav': showBottomNav }">
      <slot />
    </main>

    <!-- Bottom Navigation -->
    <nav v-if="showBottomNav" class="bottom-nav">
      <NuxtLink to="/" class="nav-item" exact-active-class="active">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="22"
          height="22"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
          <polyline points="9 22 9 12 15 12 15 22" />
        </svg>
        <span class="label">홈</span>
      </NuxtLink>
      <NuxtLink to="/blacklist" class="nav-item" active-class="active">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="22"
          height="22"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
          <line x1="12" y1="8" x2="12" y2="12" />
          <line x1="12" y1="16" x2="12.01" y2="16" />
        </svg>
        <span class="label">조회</span>
      </NuxtLink>
      <NuxtLink to="/market" class="nav-item" active-class="active">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="22"
          height="22"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <circle cx="9" cy="21" r="1" />
          <circle cx="20" cy="21" r="1" />
          <path
            d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"
          />
        </svg>
        <span class="label">장터</span>
      </NuxtLink>
      <NuxtLink to="/community" class="nav-item" active-class="active">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="22"
          height="22"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path
            d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"
          />
        </svg>
        <span class="label">소통</span>
      </NuxtLink>
    </nav>
    <CommonPwaInstallPrompt />
  </div>
</template>

<style>
/* Global Resets */
body {
  margin: 0;
  font-family:
    "Pretendard",
    -apple-system,
    BlinkMacSystemFont,
    system-ui,
    Roboto,
    "Helvetica Neue",
    "Segoe UI",
    "Apple SD Gothic Neo",
    "Malgun Gothic",
    "Apple Color Emoji",
    "Segoe UI Emoji",
    "Segoe UI Symbol",
    sans-serif;
  background-color: #121212;
  color: #ffffff;
  -webkit-font-smoothing: antialiased;
}

:root {
  --primary-color: #1e88e5;
  --secondary-color: #16213e;
  --accent-color: #c5a059;
  --danger-color: #e94560;
  --text-primary: #ffffff;
  --text-secondary: #b0b0b0;
  --glass-bg: rgba(22, 33, 62, 0.85);
  --glass-border: rgba(255, 255, 255, 0.08);
}

* {
  box-sizing: border-box;
}

.mobile-container {
  max-width: 600px;
  margin: 0 auto;
  min-height: 100vh;
  background: #121212;
  box-shadow: 0 0 40px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  position: relative;
}

/* --- Smart Header Styles --- */
.smart-header {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  position: sticky;
  top: 0;
  z-index: 1000;

  /* Glass Effect */
  background: rgba(18, 18, 18, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--glass-border);
}

.header-left,
.header-right {
  width: 40px; /* Fixed width to ensure center alignment */
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-center {
  flex: 1;
  text-align: center;
  overflow: hidden;
}

.header-center h1 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

.clickable-title:active {
  opacity: 0.7;
}

.brand-logo {
  font-size: 1.5rem;
}

.icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.icon-btn:active {
  background: rgba(255, 255, 255, 0.1);
}
.icon-btn .icon {
  font-size: 1.2rem;
}
.back-btn .icon {
  font-size: 1.1rem;
  font-weight: bold;
}

/* --- Content & Nav --- */
.content-wrapper {
  flex: 1;
  padding: 16px;
}
.content-wrapper.with-bottom-nav {
  padding-bottom: 90px;
}

.bottom-nav {
  position: fixed;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 32px);
  max-width: 568px;
  height: 64px;

  background: rgba(30, 30, 30, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;

  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 0 8px;
  z-index: 1000;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.nav-item {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-decoration: none;
  color: #888;
  gap: 4px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.nav-item span:first-child {
  font-size: 1.5rem;
  transition: transform 0.3s;
}
.nav-item .label {
  font-size: 0.7rem;
  font-weight: 500;
  opacity: 0.8;
}

.nav-item.active {
  color: var(--accent-color);
}
.nav-item.active span:first-child {
  transform: translateY(-4px);
}
.nav-item.active .label {
  opacity: 1;
  font-weight: 700;
}

/* --- Transitions --- */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(5px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
</style>
