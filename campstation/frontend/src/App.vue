<template>
  <div id="app">
    <header>
      <div class="logo">CampStation</div>
    </header>
    <main>
      <router-view />
    </main>
    <nav class="bottom-nav">
      <router-link to="/" class="nav-item">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
          />
        </svg>
        <span>홈</span>
      </router-link>
      <router-link to="/search" class="nav-item">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
          />
        </svg>
        <span>검색</span>
      </router-link>
      <router-link to="/my" class="nav-item">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
          />
        </svg>
        <span>MY</span>
      </router-link>
      <router-link to="/about" class="nav-item">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
        <span>정보</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
// App component
</script>

<style scoped>
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding-bottom: 60px; /* 하단 네비게이션 높이만큼 패딩 */
}

header {
  background-color: #2c3e50;
  padding: 1rem;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo {
  color: white;
  font-size: 1.25rem;
  font-weight: bold;
  text-align: center;
}

main {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
}

/* 하단 네비게이션 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  max-width: 640px;
  margin: 0 auto;
  background-color: #ffffff;
  border-top: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-around;
  padding: 0.5rem 0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: #666;
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
  transition: color 0.3s;
  flex: 1;
  max-width: 80px;
}

.nav-item .icon {
  width: 24px;
  height: 24px;
  margin-bottom: 0.25rem;
}

.nav-item.router-link-active {
  color: #42b983;
  font-weight: 600;
}

.nav-item:active {
  opacity: 0.7;
}

/* 모바일 터치 최적화 */
@media (hover: none) and (pointer: coarse) {
  .nav-item:hover {
    color: #666;
  }

  .nav-item.router-link-active:hover {
    color: #42b983;
  }
}
</style>
