<template>
  <div id="app">
    <header>
      <div class="logo">PSMO Community</div>
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
      <router-link to="/community" class="nav-item">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
          />
        </svg>
        <span>커뮤니티</span>
      </router-link>
      <router-link to="/write" class="nav-item nav-item-primary">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 4v16m8-8H4"
          />
        </svg>
        <span>글쓰기</span>
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
            d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
          />
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
          />
        </svg>
        <span>설정</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
// App component
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding-bottom: 60px; /* 하단 네비게이션 높이만큼 패딩 */
}

header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  font-size: 0.7rem;
  padding: 0.25rem 0.5rem;
  transition: color 0.3s;
  flex: 1;
  max-width: 70px;
}

.nav-item .icon {
  width: 24px;
  height: 24px;
  margin-bottom: 0.25rem;
}

.nav-item.router-link-active {
  color: #667eea;
  font-weight: 600;
}

/* 글쓰기 버튼 강조 */
.nav-item-primary {
  color: #667eea;
}

.nav-item-primary .icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  padding: 0.25rem;
  width: 32px;
  height: 32px;
}

.nav-item-primary.router-link-active {
  color: #764ba2;
  font-weight: 700;
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
    color: #667eea;
  }

  .nav-item-primary:hover {
    color: #667eea;
  }
}
</style>
