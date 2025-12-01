<script setup lang="ts">
import DatabaseTest from '../components/DatabaseTest.vue'
import HealthCheck from '../components/HealthCheck.vue'
</script>

<template>
  <main class="home">
    <section class="welcome-section">
      <div class="welcome-card">
        <h2>환영합니다! 👋</h2>
        <p>편리한 캠핑장 예약 서비스</p>
      </div>
    </section>

    <section class="features-section">
      <div class="feature-card">
        <div class="icon">🏕️</div>
        <h3>캠핑장 찾기</h3>
        <p>전국의 다양한 캠핑장을 검색하고 비교해보세요</p>
      </div>

      <div class="feature-card">
        <div class="icon">📅</div>
        <h3>실시간 예약</h3>
        <p>원하는 날짜에 바로 예약 가능합니다</p>
      </div>

      <div class="feature-card">
        <div class="icon">⭐</div>
        <h3>리뷰 확인</h3>
        <p>다른 캠퍼들의 생생한 후기를 확인하세요</p>
      </div>
    </section>

    <section class="database-section">
      <DatabaseTest />
    </section>

    <section class="status-section">
      <HealthCheck />
    </section>
  </main>
</template>

<style scoped>
.home {
  flex: 1;
  padding-bottom: 2rem;
}

.welcome-section {
  padding: 1rem;
}

.welcome-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
  padding: 2rem 1.5rem;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.07);
}

.welcome-card h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #2c3e50;
}

.welcome-card p {
  margin: 0.5rem 0 0 0;
  color: #546e7a;
  font-size: 0.95rem;
}

.features-section {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.feature-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.feature-card:active {
  transform: scale(0.98);
}

.icon {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.feature-card h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  color: #2c3e50;
}

.feature-card p {
  margin: 0;
  font-size: 0.9rem;
  color: #7f8c8d;
  line-height: 1.5;
}

.database-section {
  padding: 0 1rem;
  margin-bottom: 1rem;
}

.status-section {
  padding: 0 1rem;
}
</style>
