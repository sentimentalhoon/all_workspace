<script setup lang="ts">
import DatabaseTest from '../components/DatabaseTest.vue'
import HealthCheck from '../components/HealthCheck.vue'
</script>

<template>
  <main class="admin">
    <section class="header-section">
      <div class="header-card">
        <h2>🔧 관리자 패널</h2>
        <p>시스템 상태 모니터링 및 관리</p>
      </div>
    </section>

    <section class="database-section">
      <h3 class="section-title">데이터베이스 테스트</h3>
      <DatabaseTest />
    </section>

    <section class="status-section">
      <h3 class="section-title">시스템 상태</h3>
      <HealthCheck />
    </section>
  </main>
</template>

<style scoped>
.admin {
  flex: 1;
  padding-bottom: 2rem;
}

.header-section {
  padding: 1rem;
}

.header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 2rem 1.5rem;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  color: white;
}

.header-card h2 {
  margin: 0;
  font-size: 1.5rem;
}

.header-card p {
  margin: 0.5rem 0 0 0;
  font-size: 0.95rem;
  opacity: 0.9;
}

.section-title {
  margin: 0 0 1rem 0;
  padding: 0 1rem;
  font-size: 1.1rem;
  color: #2c3e50;
  font-weight: 600;
}

.database-section {
  padding: 1rem 0;
}

.status-section {
  padding: 1rem 0;
}
</style>
