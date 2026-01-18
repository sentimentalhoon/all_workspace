<script setup lang="ts">
const authStore = useAuthStore();
</script>

<template>
  <div class="page-container">
    <div class="profile-header">
      <h2>My Page</h2>
      <div class="user-card" v-if="authStore.user">
        <h3>{{ authStore.user.displayName }}</h3>
        <p class="role">{{ authStore.user.role }}</p>
        <div class="score">
          <span>Points:</span>
          <strong>{{ authStore.user.score }}</strong>
        </div>
      </div>
    </div>

    <div class="menu-list">
      <div class="menu-item">내 정보 수정</div>
      <div class="menu-item">설정</div>
      <div class="menu-item logout" @click="authStore.logout">로그아웃</div>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  padding: 20px;
}
.profile-header {
  margin-bottom: 30px;
}
.user-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-top: 10px;
}
.menu-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}
.menu-item {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-item.logout {
  color: #e94560;
  font-weight: bold;
}
</style>
