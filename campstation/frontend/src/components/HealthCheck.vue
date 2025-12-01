<script setup lang="ts">
import { onMounted, ref } from 'vue'

interface HealthStatus {
  status: string
  service?: string
  timestamp?: string
  error?: string
}

const backendHealth = ref<HealthStatus | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

const checkHealth = async () => {
  loading.value = true
  error.value = null

  try {
    const response = await fetch('http://localhost:8080/api/health')

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const data = await response.json()
    backendHealth.value = {
      status: data.status || 'UNKNOWN',
      service: data.service || 'Campstation Backend',
      timestamp: new Date().toLocaleString(),
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to connect to backend'
    backendHealth.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  checkHealth()
  // Auto refresh every 30 seconds
  setInterval(checkHealth, 30000)
})
</script>

<template>
  <div class="health-check">
    <h3>Backend Health Status</h3>

    <div v-if="loading" class="status loading">
      <span class="indicator">⏳</span>
      <span>Checking...</span>
    </div>

    <div v-else-if="error" class="status error">
      <span class="indicator">❌</span>
      <span>{{ error }}</span>
    </div>

    <div v-else-if="backendHealth" class="status" :class="backendHealth.status.toLowerCase()">
      <span class="indicator">{{ backendHealth.status === 'UP' ? '✅' : '⚠️' }}</span>
      <div class="details">
        <div><strong>Status:</strong> {{ backendHealth.status }}</div>
        <div><strong>Service:</strong> {{ backendHealth.service }}</div>
        <div><strong>Last Check:</strong> {{ backendHealth.timestamp }}</div>
      </div>
    </div>

    <button @click="checkHealth" class="refresh-btn">Refresh</button>
  </div>
</template>

<style scoped>
.health-check {
  padding: 1.5rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background-color: var(--color-background-soft);
  margin: 1rem 0;
}

h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: var(--color-heading);
}

.status {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-radius: 6px;
  margin-bottom: 1rem;
}

.status.loading {
  background-color: #f0f0f0;
}

.status.error {
  background-color: #fee;
}

.status.up {
  background-color: #efe;
}

.status.down {
  background-color: #fee;
}

.indicator {
  font-size: 1.5rem;
}

.details {
  flex: 1;
}

.details div {
  margin: 0.25rem 0;
}

.refresh-btn {
  padding: 0.5rem 1rem;
  background-color: var(--color-background-mute);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.refresh-btn:hover {
  background-color: var(--color-background);
}
</style>
