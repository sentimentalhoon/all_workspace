<script setup lang="ts">
/**
 * 기본 레이아웃 파일입니다.
 * 모든 페이지의 공통 테두리(헤더, 푸터 등)를 여기서 만듭니다.
 */
const route = useRoute();

// 로그인 페이지나 관리자 페이지에서는 하단 버튼(Bottom Nav)을 숨깁니다.
const showBottomNav = computed(
  () => route.path !== "/login" && route.path !== "/admin",
);
</script>

<template>
  <!-- 모바일 화면 크기에 맞춰서 중앙에 보여주는 컨테이너 -->
  <div class="mobile-container">
    <header>
      <h1>PSMO Manager</h1>
    </header>

    <!-- 
      <slot /> 자리에 각 페이지(Home, Market 등)의 내용이 들어갑니다. 
      하단 메뉴가 있으면, 컨텐츠가 버튼에 가려지지 않게 아래쪽에 여백(padding-bottom)을 줍니다.
    -->
    <main class="content-wrapper" :class="{ 'with-bottom-nav': showBottomNav }">
      <slot />
    </main>

    <!-- 하단 네비게이션 (메뉴 버튼들) -->
    <!-- 하단 네비게이션 (메뉴 버튼들) -->
    <nav v-if="showBottomNav" class="bottom-nav">
      <NuxtLink to="/" class="nav-item" exact-active-class="active">
        <span>🏠</span>
        <span class="label">홈</span>
      </NuxtLink>
      <NuxtLink to="/blacklist" class="nav-item" active-class="active">
        <span>🚨</span>
        <span class="label">조회</span>
      </NuxtLink>
      <NuxtLink to="/market" class="nav-item" active-class="active">
        <span>🤝</span>
        <span class="label">장터</span>
      </NuxtLink>
      <NuxtLink to="/my" class="nav-item" active-class="active">
        <span>👤</span>
        <span class="label">마이</span>
      </NuxtLink>
      <NuxtLink to="/community" class="nav-item" active-class="active">
        <span>💬</span>
        <span class="label">소통</span>
      </NuxtLink>
    </nav>
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
    "Segoe UI",
    Roboto,
    Helvetica,
    Arial,
    sans-serif;
  background-color: #121212; /* Deep Dark Background matching index.vue variable */
  color: #ffffff;
}

:root {
  --primary-color: #1e88e5;
  --secondary-color: #16213e;
  --accent-color: #c5a059; /* Gold */
  --danger-color: #e94560;
  --text-primary: #ffffff;
  --text-secondary: #b0b0b0;
  --glass-bg: rgba(255, 255, 255, 0.05);
  --glass-border: rgba(255, 255, 255, 0.1);
}

* {
  box-sizing: border-box;
}

.mobile-container {
  max-width: 600px;
  margin: 0 auto;
  min-height: 100vh;
  background: linear-gradient(
    180deg,
    #16213e 0%,
    #121212 100%
  ); /* Subtle Gradient */
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
}

header {
  padding: 1rem;
  background: rgba(22, 33, 62, 0.95); /* Semi-transparent header */
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--glass-border);
  text-align: center;
  position: sticky;
  top: 0;
  z-index: 100;
}

header h1 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--accent-color);
  letter-spacing: 1px;
}

.content-wrapper {
  flex: 1;
  padding: 1.5rem; /* Increased padding */
}

.content-wrapper.with-bottom-nav {
  padding-bottom: 90px;
}

/* Glassmorphism Bottom Nav */
.bottom-nav {
  position: fixed;
  bottom: 20px; /* Floating style */
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  max-width: 540px;
  background: rgba(22, 33, 62, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border);
  border-radius: 20px;
  display: flex;
  justify-content: space-around;
  padding: 12px 0;
  z-index: 1000;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #666;
  font-size: 0.75rem;
  gap: 4px;
  transition: all 0.3s ease;
  padding: 4px 12px;
  border-radius: 12px;
}

.nav-item span:first-child {
  font-size: 1.4rem;
  transition: transform 0.2s;
}

.nav-item.active {
  color: var(--accent-color);
  background: rgba(197, 160, 89, 0.1);
}

.nav-item.active span:first-child {
  transform: translateY(-2px);
}

/* Global Transition */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.3s;
}
.page-enter-from,
.page-leave-to {
  opacity: 0;
}
</style>
