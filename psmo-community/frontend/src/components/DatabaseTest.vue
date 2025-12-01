<template>
  <div class="db-test">
    <h2>🔌 데이터베이스 연결 테스트</h2>
    
    <div class="test-controls">
      <button @click="testAll" :disabled="loading" class="test-btn">
        {{ loading ? '테스트 중...' : '전체 테스트 실행' }}
      </button>
      <button @click="clearResults" :disabled="loading || !hasResults" class="clear-btn">
        결과 초기화
      </button>
    </div>

    <div v-if="error" class="error-box">
      ❌ {{ error }}
    </div>

    <div v-if="hasResults" class="results">
      <!-- PostgreSQL -->
      <div class="service-card" :class="{ success: results.postgres?.status === 'success' }">
        <div class="service-header">
          <h3>🐘 PostgreSQL</h3>
          <span class="status-badge" :class="results.postgres?.status === 'success' ? 'success' : 'fail'">
            {{ results.postgres?.status === 'success' ? '✓ 연결됨' : '✗ 실패' }}
          </span>
        </div>
        <div v-if="results.postgres?.status === 'success'" class="service-details">
          <p><strong>저장된 데이터:</strong> {{ results.postgres.saved?.name }}</p>
          <p><strong>값:</strong> {{ results.postgres.saved?.value }}</p>
          <p><strong>총 레코드:</strong> {{ results.postgres.count }}개</p>
          <p class="message">{{ results.postgres.message }}</p>
        </div>
      </div>

      <!-- Redis -->
      <div class="service-card" :class="{ success: results.redis?.status === 'success' }">
        <div class="service-header">
          <h3>⚡ Redis</h3>
          <span class="status-badge" :class="results.redis?.status === 'success' ? 'success' : 'fail'">
            {{ results.redis?.status === 'success' ? '✓ 연결됨' : '✗ 실패' }}
          </span>
        </div>
        <div v-if="results.redis?.status === 'success'" class="service-details">
          <p><strong>저장된 키:</strong> {{ results.redis.key }}</p>
          <p><strong>저장된 값:</strong> {{ results.redis.value }}</p>
          <p><strong>조회된 값:</strong> {{ results.redis.retrieved }}</p>
          <p><strong>일치 여부:</strong> {{ results.redis.match ? '✓' : '✗' }}</p>
          <p class="message">{{ results.redis.message }}</p>
        </div>
      </div>

      <!-- MinIO -->
      <div class="service-card" :class="{ success: results.minio?.status === 'success' }">
        <div class="service-header">
          <h3>🪣 MinIO</h3>
          <span class="status-badge" :class="results.minio?.status === 'success' ? 'success' : 'fail'">
            {{ results.minio?.status === 'success' ? '✓ 연결됨' : '✗ 실패' }}
          </span>
        </div>
        <div v-if="results.minio?.status === 'success'" class="service-details">
          <p><strong>버킷:</strong> {{ results.minio.bucket }}</p>
          <p><strong>파일:</strong> {{ results.minio.file }}</p>
          <p><strong>내용:</strong> {{ results.minio.content }}</p>
          <p class="message">{{ results.minio.message }}</p>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>연결 테스트 진행 중...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface TestResult {
  postgres?: {
    status?: string
    saved?: { name: string; value: string; id: number; createdAt: string }
    count?: number
    message?: string
  }
  redis?: {
    status?: string
    key?: string
    value?: string
    retrieved?: string
    match?: boolean
    message?: string
  }
  minio?: {
    status?: string
    bucket?: string
    file?: string
    content?: string
    message?: string
  }
}

const results = ref<TestResult>({})
const loading = ref(false)
const error = ref('')

const hasResults = computed(() => Object.keys(results.value).length > 0)

const testAll = async () => {
  loading.value = true
  error.value = ''
  results.value = {}

  try {
    const response = await fetch('http://localhost:8081/api/test/all')
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    const data = await response.json()
    results.value = data
  } catch (e) {
    error.value = e instanceof Error ? e.message : '테스트 실패'
  } finally {
    loading.value = false
  }
}

const clearResults = () => {
  results.value = {}
  error.value = ''
}
</script>

<style scoped>
.db-test {
  padding: 1.5rem;
}

h2 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

.test-controls {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.test-btn,
.clear-btn {
  flex: 1;
  padding: 0.875rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.test-btn {
  background: linear-gradient(135deg, #42a5f5 0%, #1976d2 100%);
  color: white;
}

.test-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 165, 245, 0.4);
}

.clear-btn {
  background: #e0e0e0;
  color: #666;
}

.clear-btn:hover:not(:disabled) {
  background: #d0d0d0;
}

.test-btn:disabled,
.clear-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.error-box {
  background: #fee;
  border: 1px solid #fcc;
  color: #c33;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.results {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.service-card {
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  padding: 1.25rem;
  transition: all 0.3s;
}

.service-card.success {
  border-color: #4caf50;
  background: #f1f8f4;
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.service-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.25rem;
}

.status-badge {
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 600;
}

.status-badge.success {
  background: #4caf50;
  color: white;
}

.status-badge.fail {
  background: #f44336;
  color: white;
}

.service-details {
  font-size: 0.9rem;
  line-height: 1.6;
}

.service-details p {
  margin: 0.5rem 0;
  color: #555;
}

.service-details strong {
  color: #2c3e50;
}

.message {
  margin-top: 1rem;
  padding: 0.75rem;
  background: white;
  border-radius: 6px;
  font-style: italic;
  color: #1976d2;
}

.loading {
  text-align: center;
  padding: 3rem 1rem;
}

.spinner {
  margin: 0 auto 1rem;
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #42a5f5;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
