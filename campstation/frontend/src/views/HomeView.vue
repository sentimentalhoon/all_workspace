<script setup lang="ts">
import { ref } from 'vue'

const currentBannerIndex = ref(0)
const banners = [
  {
    title: '신규 가입 고객 특별 혜택',
    subtitle: '첫 예약 시 10% 할인 쿠폰 증정',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  },
  {
    title: '직영 캠핑장 오픈',
    subtitle: '새로운 캠핑장을 먼저 만나보세요',
    background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  },
  {
    title: '겨울 캠핑 시즌',
    subtitle: '따뜻한 겨울 캠핑을 즐겨보세요',
    background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  },
]

const newCampings = [
  {
    id: 1,
    name: '라라랜드 제천점',
    location: '충북 제천시',
    image: '🏔️',
    rating: 4.8,
    reviews: 24,
  },
  {
    id: 2,
    name: '다문188',
    location: '경기 양평군',
    image: '🌲',
    rating: 4.9,
    reviews: 45,
  },
  {
    id: 3,
    name: '나무새캠핑장',
    location: '경기 포천시',
    image: '🏕️',
    rating: 4.7,
    reviews: 31,
  },
  {
    id: 4,
    name: '팔봉갯벌체험캠핑장',
    location: '충남 서산시',
    image: '🌊',
    rating: 4.6,
    reviews: 18,
  },
  {
    id: 5,
    name: '골짜구니 캠핑장',
    location: '강원 양양군',
    image: '⛰️',
    rating: 4.9,
    reviews: 52,
  },
]

const reviews = [
  {
    id: 1,
    user: '하엘핑',
    camping: '다문188',
    site: 'C구역 C-2',
    rating: 5,
    time: '4시간전',
    content:
      '두번째 방문이었는데 점점 인기가 많아져서 평일인데도 사이트가 꽉 차있어 간신히 예약했네요. 캠지기님은 역시 넘넘 친절하시고 캠핑장 관리도 아~~~주 깨끗하게 잘 되어있어 이번에도 잘 쉬고 힐링했어요.',
    photos: 2,
  },
  {
    id: 2,
    user: '빛나는짜니',
    camping: '다문188',
    site: 'C구역 C-3',
    rating: 5,
    time: '5시간전',
    content:
      '아이랑 잘 쉬고 갑니다. 사장님 친절하시고, 수도권에서 가기 딱 좋은 것 같아요. 올라가는 경사가 좀 힘들었지만, 샤워실 아이랑 둘이 씻기도 좋고 뜨신물도 잘 나오네요. 또 가겠습니다~',
    photos: 0,
  },
  {
    id: 3,
    user: '@ssang.d2',
    camping: '골짜구니 캠핑장',
    site: 'B존 B06',
    rating: 5,
    time: '5시간전',
    content:
      '두번째 방문인데도 좋았던 캠핑장. 조용히 힐링하기 좋고 캠지기님이 친절하세요. 방문당시 양양 산불로 위험한 날이었는데 캠장님께서 새벽까지 관리해주셨습니다 최고!',
    photos: 6,
  },
]

const nextBanner = () => {
  currentBannerIndex.value = (currentBannerIndex.value + 1) % banners.length
}

const prevBanner = () => {
  currentBannerIndex.value = (currentBannerIndex.value - 1 + banners.length) % banners.length
}
</script>

<template>
  <div class="home">
    <!-- Top Navigation -->
    <nav class="top-nav">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">🏕️</span>
          <span class="logo-text">Campstation</span>
        </div>
        <div class="nav-actions">
          <button class="nav-btn">로그인</button>
          <button class="nav-btn primary">회원가입</button>
        </div>
      </div>
    </nav>

    <!-- Main Banner Slider -->
    <section class="banner-slider">
      <div class="banner" :style="{ background: banners[currentBannerIndex].background }">
        <div class="banner-content">
          <h2>{{ banners[currentBannerIndex].title }}</h2>
          <p>{{ banners[currentBannerIndex].subtitle }}</p>
        </div>
        <div class="banner-controls">
          <button @click="prevBanner" class="banner-arrow">‹</button>
          <div class="banner-dots">
            <span
              v-for="(_, index) in banners"
              :key="index"
              :class="['dot', { active: index === currentBannerIndex }]"
              @click="currentBannerIndex = index"
            ></span>
          </div>
          <button @click="nextBanner" class="banner-arrow">›</button>
        </div>
      </div>
    </section>

    <!-- Category Menu -->
    <section class="category-menu">
      <div class="category-scroll">
        <button class="category-item active">직영 캠핑장</button>
        <button class="category-item">오토캠핑장</button>
        <button class="category-item">글램핑</button>
        <button class="category-item">카라반</button>
        <button class="category-item">반려동반</button>
        <button class="category-item">키즈</button>
      </div>
    </section>

    <!-- New Campings Section -->
    <section class="content-section">
      <div class="section-header">
        <h2>신규 직영 캠핑장</h2>
        <a href="#" class="more-link">더보기 ›</a>
      </div>
      <p class="section-subtitle">새로운 직영캠핑장을 먼저 확인해보세요!</p>

      <div class="camping-grid">
        <div v-for="camping in newCampings" :key="camping.id" class="camping-card">
          <div class="camping-image">
            <span class="camping-emoji">{{ camping.image }}</span>
          </div>
          <div class="camping-info">
            <div class="camping-location">캠핑장 {{ camping.location }}</div>
            <div class="camping-name">{{ camping.name }}</div>
            <div class="camping-rating">
              <span class="stars">⭐ {{ camping.rating }}</span>
              <span class="review-count">({{ camping.reviews }})</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Reviews Section -->
    <section class="content-section reviews-section">
      <div class="section-header">
        <h2>캠핑 후기</h2>
        <a href="#" class="more-link">더보기 ›</a>
      </div>

      <div class="reviews-list">
        <div v-for="review in reviews" :key="review.id" class="review-card">
          <div class="review-header">
            <div class="review-author">
              <div class="author-avatar">{{ review.user[0] }}</div>
              <div class="author-info">
                <div class="author-name">{{ review.user }}</div>
                <div class="review-meta">
                  {{ review.time }} • {{ review.camping }} • {{ review.site }}
                </div>
              </div>
            </div>
            <div class="review-rating">
              <span v-for="i in review.rating" :key="i" class="star">⭐</span>
            </div>
          </div>

          <div class="review-content">
            <div v-if="review.photos > 0" class="review-photos">
              <div class="photo-placeholder">📷</div>
              <span v-if="review.photos > 1" class="photo-count">+{{ review.photos }}</span>
            </div>
            <p class="review-text">{{ review.content }}</p>
          </div>

          <div class="review-actions">
            <button class="action-btn">👍 좋아요 0개</button>
            <button class="action-btn">💬 댓글 0개</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-links">
          <a href="#" class="footer-link">고객센터</a>
          <a href="#" class="footer-link">카카오 1:1 문의</a>
        </div>
        <div class="footer-info">
          <p>평일 10:00 ~ 17:00</p>
          <p>점심 11:50 ~ 13:00</p>
          <p class="note">* 주말 공휴일 휴무</p>
        </div>
        <div class="footer-social">
          <a href="#" class="social-link">📱 Android</a>
          <a href="#" class="social-link">🍎 iOS</a>
          <a href="#" class="social-link">📺 YouTube</a>
          <a href="#" class="social-link">📷 Instagram</a>
        </div>
        <div class="footer-legal">
          <a href="#">이용약관</a>
          <a href="#">개인정보처리방침</a>
          <a href="/admin">관리자</a>
        </div>
        <div class="footer-company">
          <p class="company-name">주식회사 Campstation</p>
          <p class="company-info">
            Campstation은 통신판매 중개자로서 통신판매의 당사자가 아니며<br />
            상품의 예약, 이용 및 환불 등과 관련한 의무와 책임은 각 판매자에게 있습니다.
          </p>
          <p class="copyright">© 2025 Campstation. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.home {
  min-height: 100vh;
  background: #f8f9fa;
}

/* Top Navigation */
.top-nav {
  background: white;
  border-bottom: 1px solid #e9ecef;
  position: sticky;
  top: 0;
  z-index: 100;
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

.nav-actions {
  display: flex;
  gap: 0.75rem;
}

.nav-btn {
  padding: 0.5rem 1.25rem;
  border: none;
  border-radius: 6px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  background: #f1f3f5;
  color: #495057;
}

.nav-btn:hover {
  background: #e9ecef;
}

.nav-btn.primary {
  background: #667eea;
  color: white;
}

.nav-btn.primary:hover {
  background: #5a67d8;
}

/* Banner Slider */
.banner-slider {
  width: 100%;
  overflow: hidden;
}

.banner {
  position: relative;
  padding: 4rem 2rem;
  color: white;
  text-align: center;
  transition: background 0.3s;
}

.banner-content h2 {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 0.75rem 0;
}

.banner-content p {
  font-size: 1.125rem;
  margin: 0;
  opacity: 0.95;
}

.banner-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.banner-arrow {
  background: rgba(255, 255, 255, 0.3);
  border: none;
  color: white;
  font-size: 2rem;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  transition: background 0.2s;
}

.banner-arrow:hover {
  background: rgba(255, 255, 255, 0.5);
}

.banner-dots {
  display: flex;
  gap: 0.5rem;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: background 0.2s;
}

.dot.active {
  background: white;
}

/* Category Menu */
.category-menu {
  background: white;
  border-bottom: 1px solid #e9ecef;
  position: sticky;
  top: 69px;
  z-index: 99;
}

.category-scroll {
  display: flex;
  overflow-x: auto;
  padding: 0.75rem 1rem;
  gap: 0.5rem;
  scrollbar-width: none;
}

.category-scroll::-webkit-scrollbar {
  display: none;
}

.category-item {
  flex-shrink: 0;
  padding: 0.625rem 1.25rem;
  border: 1px solid #dee2e6;
  border-radius: 20px;
  background: white;
  color: #495057;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.category-item:hover {
  border-color: #667eea;
  color: #667eea;
}

.category-item.active {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

/* Content Section */
.content-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2.5rem 1.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.section-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0;
}

.more-link {
  color: #868e96;
  text-decoration: none;
  font-size: 0.95rem;
  font-weight: 500;
}

.more-link:hover {
  color: #495057;
}

.section-subtitle {
  color: #868e96;
  font-size: 0.95rem;
  margin: 0 0 1.5rem 0;
}

/* Camping Grid */
.camping-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.25rem;
}

.camping-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  cursor: pointer;
}

.camping-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.camping-image {
  height: 160px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.camping-emoji {
  font-size: 4rem;
}

.camping-info {
  padding: 1rem;
}

.camping-location {
  font-size: 0.8rem;
  color: #868e96;
  margin-bottom: 0.25rem;
}

.camping-name {
  font-size: 1rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.camping-rating {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.875rem;
}

.stars {
  color: #ffd700;
  font-weight: 600;
}

.review-count {
  color: #868e96;
}

/* Reviews Section */
.reviews-section {
  background: white;
  margin: 0;
  padding: 2.5rem 1.5rem;
  max-width: 100%;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.review-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 1.25rem;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.review-author {
  display: flex;
  gap: 0.75rem;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #667eea;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1.125rem;
  flex-shrink: 0;
}

.author-info {
  flex: 1;
}

.author-name {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.review-meta {
  font-size: 0.85rem;
  color: #868e96;
}

.review-rating {
  font-size: 0.9rem;
}

.star {
  margin-left: 2px;
}

.review-content {
  margin-bottom: 1rem;
}

.review-photos {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  align-items: center;
}

.photo-placeholder {
  width: 80px;
  height: 80px;
  background: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}

.photo-count {
  font-size: 0.9rem;
  color: #495057;
  font-weight: 600;
}

.review-text {
  color: #495057;
  line-height: 1.6;
  margin: 0;
}

.review-actions {
  display: flex;
  gap: 1rem;
}

.action-btn {
  background: none;
  border: none;
  color: #868e96;
  font-size: 0.875rem;
  cursor: pointer;
  padding: 0.25rem 0;
  transition: color 0.2s;
}

.action-btn:hover {
  color: #495057;
}

/* Footer */
.footer {
  background: #2d3748;
  color: white;
  padding: 3rem 0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1.5rem;
  text-align: center;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.footer-link {
  color: white;
  text-decoration: none;
  font-weight: 500;
  font-size: 1rem;
}

.footer-link:hover {
  opacity: 0.8;
}

.footer-info {
  margin-bottom: 1.5rem;
  color: #cbd5e0;
}

.footer-info p {
  margin: 0.25rem 0;
  font-size: 0.95rem;
}

.footer-info .note {
  font-size: 0.875rem;
  color: #a0aec0;
}

.footer-social {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.social-link {
  color: #cbd5e0;
  text-decoration: none;
  font-size: 0.95rem;
  transition: color 0.2s;
}

.social-link:hover {
  color: white;
}

.footer-legal {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  margin-bottom: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #4a5568;
  flex-wrap: wrap;
}

.footer-legal a {
  color: #cbd5e0;
  text-decoration: none;
  font-size: 0.9rem;
}

.footer-legal a:hover {
  color: white;
}

.footer-company {
  color: #a0aec0;
  font-size: 0.875rem;
}

.company-name {
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #cbd5e0;
}

.company-info {
  line-height: 1.6;
  margin-bottom: 1rem;
}

.copyright {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #4a5568;
}

/* Responsive */
@media (max-width: 768px) {
  .banner-content h2 {
    font-size: 1.5rem;
  }

  .camping-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .footer-links,
  .footer-social,
  .footer-legal {
    flex-direction: column;
    gap: 0.75rem;
  }
}
</style>
