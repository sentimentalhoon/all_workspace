<template>
  <div class="my">
    <h1>MY 페이지</h1>
    <div class="profile-card">
      <div class="avatar">👤</div>
      <h2>사용자 이름</h2>
      <p class="email">user@example.com</p>
    </div>

    <div class="menu-list">
      <div class="menu-item">
        <span>예약 내역</span>
        <span class="arrow">›</span>
      </div>
      <div class="menu-item">
        <span>찜한 캠핑장</span>
        <span class="arrow">›</span>
      </div>
      <div class="menu-item">
        <span>리뷰 관리</span>
        <span class="arrow">›</span>
      </div>
      <div class="menu-item">
        <span>설정</span>
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
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.profile-card {
  background: linear-gradient(135deg, #42b983 0%, #2c3e50 100%);
  color: white;
  padding: 2rem;
  border-radius: 12px;
  text-align: center;
  margin-bottom: 2rem;
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

.menu-list {
  background: white;
  border-radius: 8px;
  overflow: hidden;
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
