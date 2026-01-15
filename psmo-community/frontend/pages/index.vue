<script setup lang="ts">
const authStore = useAuthStore();
</script>

<template>
  <div class="home-page">
    <div class="welcome-card">
      <h2>Hello, {{ authStore.user?.displayName || "User" }}! 👋</h2>
      <p>Welcome to PSMO Community.</p>

      <div class="stats-grid">
        <div class="stat-card">
          <h3>Score</h3>
          <div class="value">{{ authStore.user?.score || 0 }}</div>
        </div>
      </div>
    </div>

    <div class="menu-grid">
      <NuxtLink to="/market" class="menu-item">
        <span class="icon">💻</span>
        <span class="text">PC Market</span>
      </NuxtLink>
      <NuxtLink to="/board" class="menu-item">
        <span class="icon">📋</span>
        <span class="text">Community Board</span>
      </NuxtLink>
      <NuxtLink to="/report" class="menu-item">
        <span class="icon">⚠️</span>
        <span class="text">Report Bad User</span>
      </NuxtLink>
    </div>

    <button @click="authStore.logout" class="logout-btn">Logout</button>
  </div>
</template>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.welcome-card h2 {
  margin: 0 0 5px 0;
}

.welcome-card p {
  margin: 0;
  opacity: 0.9;
}

.stats-grid {
  margin-top: 15px;
  display: flex;
  gap: 15px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.2);
  padding: 10px 15px;
  border-radius: 8px;
}

.stat-card h3 {
  margin: 0;
  font-size: 0.8rem;
  opacity: 0.8;
}

.stat-card .value {
  font-size: 1.2rem;
  font-weight: bold;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.menu-item {
  background: white;
  padding: 20px;
  border-radius: 12px;
  text-decoration: none;
  color: #333;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.menu-item:hover {
  transform: translateY(-2px);
}

.menu-item .icon {
  font-size: 2rem;
}

.menu-item .text {
  font-weight: 600;
}

.logout-btn {
  margin-top: 20px;
  padding: 10px;
  background: transparent;
  border: 1px solid #ddd;
  border-radius: 8px;
  color: #666;
  cursor: pointer;
}
</style>
