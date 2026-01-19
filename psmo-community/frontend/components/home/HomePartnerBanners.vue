<script setup lang="ts">
// No script needed for static banners
</script>

<template>
  <section class="content-section">
    <div class="section-header">
      <h3>🏆 프리미엄 제휴</h3>
      <span class="ad-label">AD</span>
    </div>
    <div class="partner-slider">
      <!-- Card 1: Food -->
      <div class="partner-card food-theme" title="푸드 딜리버리">
        <div class="card-bg"></div>
        <div class="card-content">
          <span class="partner-badge">BEST</span>
          <div class="partner-icon">🍔</div>
          <div class="partner-text">
            <h4>푸드 딜리버리</h4>
            <p>전국 최저가 식자재</p>
          </div>
        </div>
      </div>
      <!-- Card 2: Tech -->
      <div class="partner-card tech-theme" title="컴닥터 24시">
        <div class="card-bg"></div>
        <div class="card-content">
          <span class="partner-badge">24H</span>
          <div class="partner-icon">💻</div>
          <div class="partner-text">
            <h4>컴닥터 24시</h4>
            <p>야간 긴급 출동</p>
          </div>
        </div>
      </div>
      <!-- Card 3: Design -->
      <div class="partner-card design-theme" title="공간 디자인">
        <div class="card-bg"></div>
        <div class="card-content">
          <span class="partner-badge">NEW</span>
          <div class="partner-icon">🎨</div>
          <div class="partner-text">
            <h4>공간 디자인</h4>
            <p>프리미엄 인테리어</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped lang="scss">
@use "~/assets/scss/variables" as *;

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;

  h3 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 700;
    color: $text-primary;
  }

  .ad-label {
    font-size: 0.7rem;
    color: rgba(255, 255, 255, 0.4);
    border: 1px solid rgba(255, 255, 255, 0.2);
    padding: 2px 6px;
    border-radius: 4px;
    margin-left: auto;
  }
}

.partner-slider {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding: 4px;
  padding-bottom: 12px;
  scroll-snap-type: x mandatory;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar {
    height: 0;
  }
}

.partner-card {
  position: relative;
  min-width: 260px;
  height: 140px;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  scroll-snap-align: center;
  transition: transform 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);

  &:hover {
    transform: translateY(-4px) scale(1.02);
  }

  /* Background Gradients */
  &.food-theme .card-bg {
    background: linear-gradient(135deg, #ff9966 0%, #ff5e62 100%);
  }
  &.tech-theme .card-bg {
    background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%);
  }
  &.design-theme .card-bg {
    background: linear-gradient(135deg, #4568dc 0%, #b06ab3 100%);
  }

  .card-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    opacity: 0.8;
    transition: opacity 0.3s;
  }
  &:hover .card-bg {
    opacity: 1;
  }

  .card-content {
    position: relative;
    z-index: 2;
    padding: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    background: rgba(0, 0, 0, 0.2);
    backdrop-filter: blur(2px);
  }

  .partner-badge {
    align-self: flex-start;
    padding: 4px 10px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.25);
    color: white;
    font-size: 0.7rem;
    font-weight: 800;
    backdrop-filter: blur(5px);
    border: 1px solid rgba(255, 255, 255, 0.3);
  }

  .partner-icon {
    position: absolute;
    top: 20px;
    right: 20px;
    font-size: 2.5rem;
    opacity: 0.8;
    filter: drop-shadow(0 4px 10px rgba(0, 0, 0, 0.3));
  }

  .partner-text {
    h4 {
      margin: 0;
      font-size: 1.1rem;
      color: white;
      font-weight: 700;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    }
    p {
      margin: 4px 0 0;
      font-size: 0.85rem;
      color: rgba(255, 255, 255, 0.9);
    }
  }
}
</style>
