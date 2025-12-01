<template>
  <div class="home">
    <h1>Welcome to PSMO Community</h1>
    <p class="subtitle">커뮤니티 플랫폼</p>

    <div v-if="health" class="health-check">
      <h2>🚀 Backend Status</h2>
      <div class="status-card">
        <div class="status-item">
          <span class="label">Status:</span>
          <span :class="['value', health.status]">{{ health.status }}</span>
        </div>
        <div class="status-item">
          <span class="label">Service:</span>
          <span class="value">{{ health.service }}</span>
        </div>
        <div class="status-item">
          <span class="label">Timestamp:</span>
          <span class="value">{{ health.timestamp }}</span>
        </div>
      </div>
    </div>

    <div v-else class="loading">
      <div class="spinner"></div>
      <p>Connecting to backend...</p>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { onMounted, ref } from "vue";

const health = ref(null);
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "";

const checkHealth = async () => {
  try {
    const response = await axios.get(`${apiBaseUrl}/api/health`);
    health.value = response.data;
  } catch (error) {
    console.error("Error checking health:", error);
    health.value = {
      status: "ERROR",
      service: "psmo-community-backend",
      timestamp: new Date().toISOString(),
      error: error.message,
    };
  }
};

onMounted(() => {
  checkHealth();
});
</script>

<style scoped>
.home {
  text-align: center;
}

h1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 3rem;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #666;
  font-size: 1.2rem;
  margin-bottom: 3rem;
}

.health-check {
  margin-top: 2rem;
}

.health-check h2 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.status-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  margin: 0 auto;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0;
  border-bottom: 1px solid #eee;
}

.status-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: 600;
  color: #666;
}

.value {
  color: #2c3e50;
}

.value.UP {
  color: #42b983;
  font-weight: bold;
}

.value.ERROR {
  color: #e74c3c;
  font-weight: bold;
}

.loading {
  margin-top: 3rem;
  color: #666;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
