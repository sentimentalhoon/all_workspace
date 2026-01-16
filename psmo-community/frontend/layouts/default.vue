<script setup lang="ts">
const route = useRoute();
const showBottomNav = computed(
  () => route.path !== "/login" && route.path !== "/admin"
);
</script>

<template>
  <div class="mobile-container">
    <header>
      <h1>PSMO Community</h1>
    </header>

    <main class="content-wrapper" :class="{ 'with-bottom-nav': showBottomNav }">
      <slot />
    </main>

    <nav v-if="showBottomNav" class="bottom-nav">
      <NuxtLink to="/" class="nav-item" exact-active-class="active">
        <span>🏠</span>
        <span class="label">홈</span>
      </NuxtLink>
      <NuxtLink to="/market" class="nav-item" active-class="active">
        <span>💻</span>
        <span class="label">장터</span>
      </NuxtLink>
      <NuxtLink to="/board" class="nav-item" active-class="active">
        <span>📋</span>
        <span class="label">게시판</span>
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
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica,
    Arial, sans-serif;
  background-color: #f0f2f5;
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
  color: #333;
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
  color: #2196f3;
}
</style>
