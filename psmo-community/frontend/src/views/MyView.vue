<template>
  <div class="my-view">
    <div class="page-header">
      <h2>👤 마이페이지</h2>
    </div>

    <div class="my-container">
      <div class="profile-section">
        <div class="profile-avatar">
          <img src="https://via.placeholder.com/80" alt="프로필" />
        </div>
        <div class="profile-info">
          <p class="login-prompt">로그인이 필요합니다</p>
          <button class="btn-login">로그인 / 회원가입</button>
        </div>
      </div>

      <div class="stats-section">
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">작성글</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">댓글</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">받은 좋아요</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">팔로워</div>
        </div>
      </div>

      <div class="menu-section">
        <h3>활동</h3>
        <div class="menu-items">
          <button class="menu-item">
            <span class="menu-icon">✍️</span>
            <span class="menu-label">내가 쓴 글</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">⚠️</span>
            <span class="menu-label">등록한 진상</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">💾</span>
            <span class="menu-label">저장한 글</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">🖥️</span>
            <span class="menu-label">내 피씨방</span>
            <span class="menu-arrow">›</span>
          </button>
        </div>
      </div>

      <div class="menu-section">
        <h3>피씨방 관리</h3>
        <div class="menu-items">
          <button class="menu-item">
            <span class="menu-icon">🔔</span>
            <span class="menu-label">진상 알림 설정</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">📊</span>
            <span class="menu-label">진상 통계</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">🚫</span>
            <span class="menu-label">블랙리스트</span>
            <span class="menu-arrow">›</span>
          </button>
        </div>
      </div>

      <div class="menu-section">
        <h3>설정</h3>
        <div class="menu-items">
          <button class="menu-item">
            <span class="menu-icon">👤</span>
            <span class="menu-label">프로필 수정</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">🔒</span>
            <span class="menu-label">개인정보 설정</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">⚙️</span>
            <span class="menu-label">앱 설정</span>
            <span class="menu-arrow">›</span>
          </button>
        </div>
      </div>

      <div class="menu-section">
        <h3>지원</h3>
        <div class="menu-items">
          <button class="menu-item">
            <span class="menu-icon">📢</span>
            <span class="menu-label">공지사항</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">❓</span>
            <span class="menu-label">FAQ</span>
            <span class="menu-arrow">›</span>
          </button>
          <button class="menu-item">
            <span class="menu-icon">📧</span>
            <span class="menu-label">문의하기</span>
            <span class="menu-arrow">›</span>
          </button>
        </div>
      </div>

      <div class="footer-links">
        <a href="#">이용약관</a>
        <span class="divider">|</span>
        <a href="#">개인정보처리방침</a>
        <span class="divider">|</span>
        <RouterLink to="/admin">관리자</RouterLink>
      </div>

      <div class="app-info">
        <p>PSMO Community v1.0.0</p>
        <p>© 2024 PSMO. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.my-view {
  background: #f8f9fa;
  min-height: calc(100vh - 120px);
}

.page-header {
  background: white;
  padding: 1.5rem 1rem;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.page-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #2d3748;
}

.my-container {
  padding: 1rem;
}

.profile-section {
  background: white;
  padding: 2rem 1rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.profile-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 1rem;
  border: 3px solid #667eea;
}

.profile-info .login-prompt {
  margin: 0 0 1rem 0;
  color: #666;
  font-size: 0.95rem;
}

.btn-login {
  padding: 0.75rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  font-size: 1rem;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
  background: white;
  padding: 1rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.75rem;
  color: #666;
}

.menu-section {
  background: white;
  padding: 1rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.menu-section h3 {
  margin: 0 0 0.75rem 0;
  font-size: 0.9rem;
  color: #666;
  font-weight: 600;
  padding-left: 0.5rem;
}

.menu-items {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 1rem 0.5rem;
  background: none;
  border: none;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
  text-align: left;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-icon {
  font-size: 1.25rem;
  margin-right: 0.75rem;
}

.menu-label {
  flex: 1;
  font-size: 0.95rem;
  color: #2d3748;
}

.menu-arrow {
  font-size: 1.25rem;
  color: #ccc;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin: 2rem 0 1rem 0;
  font-size: 0.85rem;
}

.footer-links a {
  color: #666;
  text-decoration: none;
}

.footer-links .divider {
  color: #ccc;
}

.app-info {
  text-align: center;
  color: #999;
  font-size: 0.8rem;
  padding-bottom: 1rem;
}

.app-info p {
  margin: 0.25rem 0;
}
</style>
