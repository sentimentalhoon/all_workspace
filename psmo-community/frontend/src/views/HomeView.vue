<script setup lang="ts">
import { ref } from 'vue'

const stats = ref({
  totalReports: 1247,
  pcRooms: 89,
  activeUsers: 3542,
})

const recentReports = ref([
  {
    id: 1,
    type: '욕설/폭언',
    region: '서울',
    pcRoom: '강남게임존',
    time: '10분 전',
    severity: 'high',
  },
  {
    id: 2,
    type: '미결제',
    region: '경기',
    pcRoom: '수원PC클럽',
    time: '1시간 전',
    severity: 'critical',
  },
  {
    id: 3,
    type: '불결',
    region: '인천',
    pcRoom: '부평게임타운',
    time: '2시간 전',
    severity: 'medium',
  },
])

const hotPosts = ref([
  {
    id: 1,
    title: '요즘 피씨방 물가 너무 오른거 아님?',
    comments: 45,
    likes: 23,
  },
  {
    id: 2,
    title: '강남 근처 괜찮은 피씨방 추천해주세요',
    comments: 32,
    likes: 18,
  },
  {
    id: 3,
    title: '피씨방 알바 10년차가 알려주는 꿀팁',
    comments: 67,
    likes: 89,
  },
])
</script>

<template>
  <main class="home">
    <section class="welcome-section">
      <div class="welcome-card">
        <h2>🎮 PSMO 커뮤니티</h2>
        <p>성인 게임 피씨방 정보 공유 & 진상 고객 등록</p>
      </div>
    </section>

    <section class="stats-section">
      <div class="stat-card">
        <div class="stat-number">{{ stats.totalReports.toLocaleString() }}</div>
        <div class="stat-label">진상 등록</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.pcRooms }}</div>
        <div class="stat-label">피씨방</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.activeUsers.toLocaleString() }}</div>
        <div class="stat-label">활성 유저</div>
      </div>
    </section>

    <section class="quick-links">
      <RouterLink to="/report" class="quick-link danger">
        <span class="link-icon">⚠️</span>
        <span class="link-text">진상 등록하기</span>
        <span class="link-arrow">›</span>
      </RouterLink>
      <RouterLink to="/board" class="quick-link primary">
        <span class="link-icon">💬</span>
        <span class="link-text">게시판 보기</span>
        <span class="link-arrow">›</span>
      </RouterLink>
    </section>

    <section class="recent-section">
      <div class="section-header">
        <h3>⚠️ 최근 등록된 진상</h3>
        <RouterLink to="/report" class="view-more">더보기 ›</RouterLink>
      </div>
      <div class="report-list">
        <div v-for="report in recentReports" :key="report.id" class="report-item">
          <div class="report-main">
            <span class="report-type">{{ report.type }}</span>
            <span class="report-location">📍 {{ report.region }} {{ report.pcRoom }}</span>
          </div>
          <div class="report-meta">
            <span :class="['severity-badge', `severity-${report.severity}`]">
              {{
                report.severity === 'critical'
                  ? '매우심각'
                  : report.severity === 'high'
                    ? '심각'
                    : '보통'
              }}
            </span>
            <span class="report-time">{{ report.time }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="hot-section">
      <div class="section-header">
        <h3>🔥 인기 게시글</h3>
        <RouterLink to="/board" class="view-more">더보기 ›</RouterLink>
      </div>
      <div class="post-list">
        <div v-for="post in hotPosts" :key="post.id" class="post-item">
          <div class="post-title">{{ post.title }}</div>
          <div class="post-stats">
            <span>💬 {{ post.comments }}</span>
            <span>❤️ {{ post.likes }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="features-section">
      <div class="feature-card">
        <div class="icon">🖥️</div>
        <h3>피씨방 정보</h3>
        <p>전국 피씨방 정보와 리뷰를 확인하세요</p>
      </div>

      <div class="feature-card">
        <div class="icon">🎮</div>
        <h3>게임 토크</h3>
        <p>다양한 게임에 대해 이야기 나눠보세요</p>
      </div>

      <div class="feature-card">
        <div class="icon">💡</div>
        <h3>팁 & 노하우</h3>
        <p>피씨방 이용 팁과 게임 노하우 공유</p>
      </div>
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 2rem 1.5rem;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.welcome-card h2 {
  margin: 0;
  font-size: 1.5rem;
  color: white;
}

.welcome-card p {
  margin: 0.5rem 0 0 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9rem;
}

.stats-section {
  padding: 1rem;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.75rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.25rem 0.75rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-number {
  font-size: 1.5rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.8rem;
  color: #666;
}

.quick-links {
  padding: 0 1rem;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.quick-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.quick-link.danger {
  background: #dc3545;
  color: white;
}

.quick-link.danger:hover {
  background: #c82333;
  transform: translateY(-2px);
}

.quick-link.primary {
  background: #667eea;
  color: white;
}

.quick-link.primary:hover {
  background: #5568d3;
  transform: translateY(-2px);
}

.link-icon {
  font-size: 1.25rem;
}

.link-text {
  flex: 1;
  margin-left: 0.5rem;
}

.link-arrow {
  font-size: 1.25rem;
}

.recent-section,
.hot-section {
  padding: 0 1rem;
  margin-bottom: 1.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.section-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #2d3748;
}

.view-more {
  font-size: 0.85rem;
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.report-list,
.post-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.report-item {
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.report-item:last-child {
  border-bottom: none;
}

.report-main {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-bottom: 0.5rem;
}

.report-type {
  font-weight: 600;
  color: #dc3545;
  font-size: 0.9rem;
}

.report-location {
  font-size: 0.85rem;
  color: #666;
}

.report-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.severity-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 600;
  color: white;
}

.severity-critical {
  background: #dc3545;
}

.severity-high {
  background: #fd7e14;
}

.severity-medium {
  background: #ffc107;
}

.report-time {
  font-size: 0.75rem;
  color: #999;
}

.post-item {
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.post-item:last-child {
  border-bottom: none;
}

.post-item:hover {
  background: #f8f9fa;
}

.post-title {
  font-size: 0.9rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.post-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
  color: #999;
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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
  color: #2d3748;
}

.feature-card p {
  margin: 0;
  font-size: 0.9rem;
  color: #7f8c8d;
  line-height: 1.5;
}
</style>
