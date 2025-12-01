<script setup lang="ts">
import { ref } from 'vue'

const searchQuery = ref('')
const checkIn = ref('')
const checkOut = ref('')

const handleSearch = () => {
  console.log('Searching:', { searchQuery: searchQuery.value, checkIn: checkIn.value, checkOut: checkOut.value })
}
</script>

<template>
  <div class="home">
    <!-- Navigation -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">🏕️</span>
          <span class="logo-text">Campstation</span>
        </div>
        <div class="nav-links">
          <a href="#" class="nav-link">로그인</a>
          <a href="#" class="nav-link primary">회원가입</a>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">자연 속 완벽한 휴식,<br />캠핑장 예약의 시작</h1>
        <p class="hero-subtitle">전국 캠핑장을 한눈에 비교하고 실시간으로 예약하세요</p>
        
        <!-- Search Box -->
        <div class="search-box">
          <div class="search-input-group">
            <div class="input-wrapper">
              <label class="input-label">캠핑장 검색</label>
              <input
                v-model="searchQuery"
                type="text"
                placeholder="지역, 캠핑장 이름으로 검색"
                class="search-input"
              />
            </div>
            <div class="date-inputs">
              <div class="input-wrapper">
                <label class="input-label">체크인</label>
                <input v-model="checkIn" type="date" class="date-input" />
              </div>
              <div class="input-wrapper">
                <label class="input-label">체크아웃</label>
                <input v-model="checkOut" type="date" class="date-input" />
              </div>
            </div>
          </div>
          <button @click="handleSearch" class="search-button">
            <span>검색하기</span>
            <span class="search-icon">🔍</span>
          </button>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <div class="container">
        <h2 class="section-title">왜 Campstation인가요?</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">🗺️</div>
            <h3>전국 캠핑장 정보</h3>
            <p>전국 각지의 검증된 캠핑장 정보를 한곳에서 확인하세요</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">⚡</div>
            <h3>실시간 예약</h3>
            <p>실시간 예약 가능 여부를 확인하고 즉시 예약하세요</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">💰</div>
            <h3>최저가 보장</h3>
            <p>다양한 캠핑장의 가격을 비교하고 합리적인 선택을 하세요</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">⭐</div>
            <h3>실제 이용 후기</h3>
            <p>검증된 이용자들의 생생한 리뷰를 확인하세요</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Popular Destinations -->
    <section class="destinations">
      <div class="container">
        <h2 class="section-title">인기 캠핑 지역</h2>
        <div class="destinations-grid">
          <div class="destination-card">
            <div class="destination-image" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <span class="destination-emoji">🏔️</span>
            </div>
            <div class="destination-info">
              <h3>강원도</h3>
              <p>120개 캠핑장</p>
            </div>
          </div>
          <div class="destination-card">
            <div class="destination-image" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <span class="destination-emoji">🌊</span>
            </div>
            <div class="destination-info">
              <h3>경상도</h3>
              <p>85개 캠핑장</p>
            </div>
          </div>
          <div class="destination-card">
            <div class="destination-image" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <span class="destination-emoji">🏝️</span>
            </div>
            <div class="destination-info">
              <h3>제주도</h3>
              <p>45개 캠핑장</p>
            </div>
          </div>
          <div class="destination-card">
            <div class="destination-image" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
              <span class="destination-emoji">🌲</span>
            </div>
            <div class="destination-info">
              <h3>경기도</h3>
              <p>95개 캠핑장</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta">
      <div class="container">
        <div class="cta-content">
          <h2>지금 바로 시작하세요</h2>
          <p>회원가입하고 첫 예약 시 특별 할인 혜택을 받으세요</p>
          <button class="cta-button">무료로 시작하기</button>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-brand">
            <span class="logo-icon">🏕️</span>
            <span class="logo-text">Campstation</span>
          </div>
          <div class="footer-links">
            <a href="#">서비스 소개</a>
            <a href="#">이용약관</a>
            <a href="#">개인정보처리방침</a>
            <a href="/admin">관리자</a>
          </div>
          <p class="footer-copyright">© 2025 Campstation. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background: #fff;
}

/* Navigation */
.navbar {
  background: white;
  border-bottom: 1px solid #e9ecef;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 700;
  font-size: 1.25rem;
  color: #2d3748;
}

.logo-icon {
  font-size: 1.5rem;
}

.nav-links {
  display: flex;
  gap: 1rem;
}

.nav-link {
  text-decoration: none;
  color: #4a5568;
  font-size: 0.95rem;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  transition: all 0.2s;
}

.nav-link:hover {
  background: #f7fafc;
  color: #2d3748;
}

.nav-link.primary {
  background: #667eea;
  color: white;
}

.nav-link.primary:hover {
  background: #5a67d8;
}

/* Hero Section */
.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4rem 1.5rem;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 2.5rem;
  font-weight: 800;
  margin: 0 0 1rem 0;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.125rem;
  margin: 0 0 2.5rem 0;
  opacity: 0.95;
}

/* Search Box */
.search-box {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  max-width: 700px;
  margin: 0 auto;
}

.search-input-group {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1rem;
}

.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #4a5568;
  text-align: left;
}

.search-input {
  width: 100%;
  padding: 0.875rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
}

.date-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.date-input {
  width: 100%;
  padding: 0.875rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: border-color 0.2s;
}

.date-input:focus {
  outline: none;
  border-color: #667eea;
}

.search-button {
  width: 100%;
  background: #667eea;
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 8px;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.search-button:hover {
  background: #5a67d8;
}

.search-icon {
  font-size: 1.25rem;
}

/* Container */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1.5rem;
}

/* Sections */
.section-title {
  font-size: 2rem;
  font-weight: 700;
  text-align: center;
  margin: 0 0 3rem 0;
  color: #2d3748;
}

/* Features Section */
.features {
  padding: 5rem 0;
  background: #f8f9fa;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0 0 0.75rem 0;
  color: #2d3748;
}

.feature-card p {
  margin: 0;
  color: #718096;
  line-height: 1.6;
}

/* Destinations Section */
.destinations {
  padding: 5rem 0;
  background: white;
}

.destinations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 1.5rem;
}

.destination-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.destination-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.destination-image {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.destination-emoji {
  font-size: 4rem;
}

.destination-info {
  padding: 1.5rem;
}

.destination-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
  font-weight: 700;
  color: #2d3748;
}

.destination-info p {
  margin: 0;
  color: #718096;
  font-size: 0.95rem;
}

/* CTA Section */
.cta {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 5rem 0;
}

.cta-content {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

.cta-content h2 {
  font-size: 2.25rem;
  font-weight: 800;
  margin: 0 0 1rem 0;
}

.cta-content p {
  font-size: 1.125rem;
  margin: 0 0 2rem 0;
  opacity: 0.95;
}

.cta-button {
  background: white;
  color: #667eea;
  border: none;
  padding: 1rem 2.5rem;
  border-radius: 8px;
  font-size: 1.125rem;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.cta-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

/* Footer */
.footer {
  background: #2d3748;
  color: white;
  padding: 3rem 0 2rem;
}

.footer-content {
  text-align: center;
}

.footer-brand {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 700;
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.footer-links a {
  color: #cbd5e0;
  text-decoration: none;
  font-size: 0.95rem;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: white;
}

.footer-copyright {
  color: #a0aec0;
  font-size: 0.875rem;
  margin: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title {
    font-size: 1.875rem;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .features-grid,
  .destinations-grid {
    grid-template-columns: 1fr;
  }

  .date-inputs {
    grid-template-columns: 1fr;
  }
}
</style>
