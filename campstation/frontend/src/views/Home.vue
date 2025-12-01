<template>
  <div class="home">
    <h1>Welcome to Campstation</h1>
    <div v-if="health" class="health-check">
      <h2>Backend Status</h2>
      <p>
        Status: <span :class="health.status">{{ health.status }}</span>
      </p>
      <p>Service: {{ health.service }}</p>
      <p>Timestamp: {{ health.timestamp }}</p>
    </div>
    <div v-else class="loading">Loading backend status...</div>
  </div>
</template>

<script setup>
import axios from "axios";
import { onMounted, ref } from "vue";

const health = ref(null);

const checkHealth = async () => {
  try {
    const response = await axios.get("/api/health");
    health.value = response.data;
  } catch (error) {
    console.error("Error checking health:", error);
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
  color: #42b983;
  margin-bottom: 2rem;
}

.health-check {
  background: #f5f5f5;
  padding: 2rem;
  border-radius: 8px;
  max-width: 400px;
  margin: 0 auto;
}

.health-check h2 {
  margin-bottom: 1rem;
}

.health-check p {
  margin: 0.5rem 0;
}

.UP {
  color: #42b983;
  font-weight: bold;
}

.loading {
  color: #666;
}
</style>
