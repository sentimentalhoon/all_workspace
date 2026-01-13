<script setup lang="ts">
import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'

import GlobalToast from './components/GlobalToast.vue'
import PwaInstallPrompt from './components/PwaInstallPrompt.vue'

const route = useRoute()
const showBottomNav = computed(() => route.path !== '/admin' && route.path !== '/login')
</script>

<template>
  <div class="mobile-container">
    <div class="content-wrapper" :class="{ 'with-bottom-nav': showBottomNav }">
      <RouterView />
      <PwaInstallPrompt :bottom-offset="showBottomNav ? 96 : 24" mobile-only />
      <GlobalToast />
    </div>

    <nav v-if="showBottomNav" class="bottom-nav">
      <RouterLink to="/" class="nav-item" :class="{ active: route.path === '/' }">
        <span class="nav-icon">🏠</span>
        <span class="nav-label">홈</span>
      </RouterLink>
      <RouterLink to="/report" class="nav-item" :class="{ active: route.path === '/report' }">
        <span class="nav-icon">⚠️</span>
        <span class="nav-label">진상등록</span>
      </RouterLink>
      <RouterLink to="/chat" class="nav-item" :class="{ active: route.path === '/chat' }">
        <span class="nav-icon">💬</span>
        <span class="nav-label">채팅</span>
      </RouterLink>

      <RouterLink to="/board" class="nav-item" :class="{ active: route.path === '/board' }">
        <span class="nav-icon">📋</span>
        <span class="nav-label">게시판</span>
      </RouterLink>
      <RouterLink to="/my" class="nav-item" :class="{ active: route.path === '/my' }">
        <span class="nav-icon">👤</span>
        <span class="nav-label">마이</span>
      </RouterLink>
    </nav>
  </div>
</template>

<style scoped>
.mobile-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  max-width: 640px;
  margin: 0 auto;
  position: relative;
  background: white;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem 1rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

header h1 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
}

.subtitle {
  margin: 0.5rem 0 0 0;
  font-size: 0.9rem;
  opacity: 0.9;
}

.content-wrapper {
  flex: 1;
}

.content-wrapper.with-bottom-nav {
  padding-bottom: 60px;
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  max-width: 640px;
  width: 100%;
  background: white;
  border-top: 1px solid #e0e0e0;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  padding: 0.4rem 0;
  z-index: 1000;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0.4rem;
  text-decoration: none;
  color: #666;
  transition: all 0.2s;
}

.nav-item:hover {
  background: #f5f5f5;
}

.nav-item.active {
  color: #667eea;
}

.nav-icon {
  font-size: 1.25rem;
  margin-bottom: 0.2rem;
}

.nav-label {
  font-size: 0.7rem;
  font-weight: 500;
}
</style>
