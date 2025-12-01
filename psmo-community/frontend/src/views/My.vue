<template>
  <div class="my">
    <h1>MY</h1>

    <div class="profile-card">
      <div class="avatar">👤</div>
      <h2>사용자 이름</h2>
      <p class="email">user@example.com</p>
    </div>

    <div class="stats-grid">
      <div class="stat-item">
        <div class="stat-number">12</div>
        <div class="stat-label">작성글</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">34</div>
        <div class="stat-label">댓글</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">56</div>
        <div class="stat-label">좋아요</div>
      </div>
    </div>

    <div class="menu-list">
      <div class="menu-item">
        <span>내가 쓴 글</span>
        <span class="arrow">›</span>
      </div>
      <div class="menu-item">
        <span>내가 쓴 댓글</span>
        <span class="arrow">›</span>
      </div>
      <div class="menu-item">
        <span>북마크</span>
        <span class="arrow">›</span>
      </div>
      <div class="menu-item">
        <span>알림 설정</span>
        <span class="arrow">›</span>
      </div>
    </div>
  </div>
</template>

<script setup>
// My page component
</script>

<style scoped>
.my {
  max-width: 600px;
  margin: 0 auto;
}

h1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 1.5rem;
}

.profile-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem;
  border-radius: 12px;
  text-align: center;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

.avatar {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.profile-card h2 {
  margin-bottom: 0.5rem;
  color: white;
}

.email {
  opacity: 0.9;
  font-size: 0.9rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-item {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-number {
  font-size: 1.5rem;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.85rem;
  color: #666;
}

.menu-list {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:active {
  background-color: #f5f5f5;
}

.arrow {
  color: #999;
  font-size: 1.5rem;
}
</style>
