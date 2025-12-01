<script setup lang="ts">
import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'

const route = useRoute()

const showBottomNav = computed(() => {
  // Admin 페이지에서는 bottom nav 숨김
  return route.name !== 'admin'
})
</script>

<template>
  <div class="app-container">
    <div class="content-wrapper" :class="{ 'with-bottom-nav': showBottomNav }">
      <RouterView />
    </div>

    <!-- Bottom Navigation -->
    <nav v-if="showBottomNav" class="bottom-nav">
      <RouterLink to="/" class="nav-item" :class="{ active: route.name === 'home' }">
        <span class="nav-icon">🏕️</span>
        <span class="nav-label">홈</span>
      </RouterLink>
      <RouterLink to="/search" class="nav-item" :class="{ active: route.name === 'search' }">
        <span class="nav-icon">🔍</span>
        <span class="nav-label">검색</span>
      </RouterLink>
      <RouterLink to="/bookings" class="nav-item" :class="{ active: route.name === 'bookings' }">
        <span class="nav-icon">📅</span>
        <span class="nav-label">예약내역</span>
      </RouterLink>
      <RouterLink to="/my" class="nav-item" :class="{ active: route.name === 'my' }">
        <span class="nav-icon">👤</span>
        <span class="nav-label">마이</span>
      </RouterLink>
    </nav>
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  background: white;
  display: flex;
  flex-direction: column;
  max-width: 640px;
  margin: 0 auto;
  position: relative;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.content-wrapper {
  flex: 1;
  overflow-y: auto;
  background: #f8f9fa;
}

.content-wrapper.with-bottom-nav {
  padding-bottom: 60px;
}

/* Bottom Navigation */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  max-width: 640px;
  width: 100%;
  background: white;
  border-top: 1px solid #e9ecef;
  display: flex;
  justify-content: space-around;
  padding: 0.4rem 0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: 0.4rem;
  text-decoration: none;
  color: #868e96;
  transition: color 0.2s;
  gap: 0.2rem;
}

.nav-item:hover {
  color: #495057;
}

.nav-item.active {
  color: #667eea;
}

.nav-icon {
  font-size: 1.25rem;
  line-height: 1;
}

.nav-label {
  font-size: 0.7rem;
  font-weight: 500;
}
</style>
