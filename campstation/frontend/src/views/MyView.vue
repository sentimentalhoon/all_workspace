<script setup lang="ts">
const menuItems = [
  { icon: '👤', label: '프로필 수정', path: '/profile' },
  { icon: '⭐', label: '내가 쓴 후기', path: '/my-reviews' },
  { icon: '❤️', label: '찜한 캠핑장', path: '/favorites' },
  { icon: '🎫', label: '쿠폰', path: '/coupons' },
  { icon: '📢', label: '공지사항', path: '/notices' },
  { icon: '❓', label: '자주 묻는 질문', path: '/faq' },
  { icon: '⚙️', label: '설정', path: '/settings' },
]
</script>

<template>
  <div class="my-page">
    <header class="page-header">
      <h1>마이페이지</h1>
    </header>

    <div class="profile-section">
      <div class="profile-avatar">👤</div>
      <div class="profile-info">
        <h2>로그인이 필요합니다</h2>
        <p>캠핑장 예약과 다양한 혜택을 받아보세요</p>
      </div>
      <button class="login-btn">로그인 / 회원가입</button>
    </div>

    <div class="menu-section">
      <div class="menu-list">
        <a v-for="item in menuItems" :key="item.path" :href="item.path" class="menu-item">
          <span class="menu-icon">{{ item.icon }}</span>
          <span class="menu-label">{{ item.label }}</span>
          <span class="menu-arrow">›</span>
        </a>
      </div>
    </div>

    <div class="info-section">
      <div class="info-links">
        <a href="#">이용약관</a>
        <span class="separator">|</span>
        <a href="#">개인정보처리방침</a>
        <span class="separator">|</span>
        <a href="/admin">관리자</a>
      </div>
      <div class="app-info">
        <p>버전 1.0.0</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.my-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  background: white;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
  position: sticky;
  top: 0;
  z-index: 10;
}

.page-header h1 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
  color: #2d3748;
}

.profile-section {
  background: white;
  padding: 2rem 1.5rem;
  text-align: center;
  border-bottom: 8px solid #f8f9fa;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto 1rem;
  background: #e9ecef;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
}

.profile-info h2 {
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
  color: #2d3748;
}

.profile-info p {
  margin: 0 0 1.5rem 0;
  color: #868e96;
  font-size: 0.95rem;
}

.login-btn {
  width: 100%;
  max-width: 280px;
  padding: 0.875rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.login-btn:hover {
  background: #5a67d8;
}

.menu-section {
  background: white;
  margin-bottom: 1rem;
}

.menu-list {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 1rem 1.5rem;
  text-decoration: none;
  color: #2d3748;
  border-bottom: 1px solid #f1f3f5;
  transition: background 0.2s;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 1.25rem;
  width: 32px;
  text-align: center;
}

.menu-label {
  flex: 1;
  margin-left: 1rem;
  font-size: 0.95rem;
  font-weight: 500;
}

.menu-arrow {
  color: #adb5bd;
  font-size: 1.25rem;
}

.info-section {
  padding: 2rem 1.5rem;
  text-align: center;
  color: #868e96;
  font-size: 0.875rem;
}

.info-links {
  margin-bottom: 1rem;
}

.info-links a {
  color: #868e96;
  text-decoration: none;
}

.info-links a:hover {
  color: #495057;
}

.separator {
  margin: 0 0.5rem;
  color: #dee2e6;
}

.app-info p {
  margin: 0;
  color: #adb5bd;
}
</style>
