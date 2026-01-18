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
    </nav>
  </div>
</template>

<style>
/* Global Resets */
body {
  margin: 0;
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial,
    sans-serif;
  background-color: #1a1a2e; /* Dark Navy Background */
  color: #333;
}

:root {
  --primary-color: #c5a059; /* Gold */
  --secondary-color: #16213e; /* Navy */
  --accent-color: #e94560; /* Red */
  --text-dark: #333;
  --text-light: #f5f5f5;
  --bg-light: #f0f2f5;
}

* {
  box-sizing: border-box;
}

.mobile-container {
  max-width: 600px;
  margin: 0 auto;
  min-height: 100vh;
  background: white;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

header {
  padding: 1rem;
  background: white;
  border-bottom: 1px solid #eee;
  text-align: center;
}

header h1 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--secondary-color);
}

.content-wrapper {
  flex: 1;
  padding: 1rem;
}

.content-wrapper.with-bottom-nav {
  padding-bottom: 80px;
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  margin: 0 auto;
  width: 100%;
  max-width: 600px;
  background: white;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #888;
  font-size: 0.8rem;
  gap: 4px;
}

.nav-item.active {
  color: var(--secondary-color);
  font-weight: bold;
}
</style>
