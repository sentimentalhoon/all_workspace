<template>
  <div class="chat-view">
    <section class="hero">
      <div class="hero-copy">
        <p class="badge">Live · Telegram</p>
        <h1>PSMO Community Chat</h1>
        <p class="summary">
          실시간 대화와 현장 소식을 놓치지 마세요. 텔레그램 위젯을 통해 모바일에서도 쾌적하게 채팅할 수 있도록
          최적화했습니다.
        </p>
        <div class="hero-actions">
          <a class="cta primary" href="https://t.me/psmocommunity" target="_blank" rel="noreferrer">
            채널 바로가기
          </a>
          <button class="cta ghost" type="button" @click="scrollToWidget">채팅 영역으로 이동</button>
        </div>
        <div class="hero-metrics">
          <span>24/7 Live</span>
          <span>알림 지원</span>
          <span>모바일 최적화</span>
        </div>
      </div>
    </section>

    <section class="status-grid">
      <article class="status-card">
        <div>
          <p class="label">현재 상태</p>
          <p class="value online">Online</p>
        </div>
        <p class="desc">운영팀이 실시간으로 모니터링하며 안전한 공간을 유지합니다.</p>
      </article>
      <article class="status-card">
        <div>
          <p class="label">인기 주제</p>
          <p class="value">동행 구하기 · 캠핑팁 · 중고거래</p>
        </div>
      </article>
      <article class="status-card">
        <div>
          <p class="label">필수 조건</p>
          <p class="value">텔레그램 계정 + PSMO 가입</p>
        </div>
      </article>
    </section>

    <section class="widget-wrapper" id="chat-widget">
      <header>
        <div>
          <h2>커뮤니티 채팅</h2>
          <p>@psmocommunity · Telegram Discussion Widget</p>
        </div>
        <button type="button" class="refresh-btn" @click="reloadWidget">새로고침</button>
      </header>
      <div id="telegram-discussion" class="widget-shell">
        <p class="loading-text">채팅 위젯을 불러오는 중입니다...</p>
      </div>
    </section>

    <section class="guide-card">
      <h3>빠른 입장 가이드</h3>
      <ol>
        <li>위젯 하단에서 <strong>댓글 작성</strong>을 눌러 로그인합니다.</li>
        <li>처음이라면 텔레그램 앱 설치 후 계정을 연결합니다.</li>
        <li>운영 가이드(광고 금지, 개인정보 보호)를 준수해 주세요.</li>
        <li>개인 거래/동행은 DM으로만 진행해 주세요.</li>
      </ol>
    </section>

    <section class="app-section">
      <div class="promo-copy">
        <h3>앱에서도 더 빠르게</h3>
        <p>텔레그램 앱을 설치하면 알림과 다중 디바이스 동기화가 더욱 편리해집니다.</p>
        <div class="download-buttons">
          <a
            class="download android"
            href="https://play.google.com/store/apps/details?id=org.telegram.messenger"
            target="_blank"
            rel="noreferrer"
            >Android</a
          >
          <a
            class="download ios"
            href="https://apps.apple.com/app/telegram-messenger/id686449807"
            target="_blank"
            rel="noreferrer"
            >iOS</a
          >
        </div>
      </div>
      <div class="promo-visual">📱</div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted } from 'vue'

const WIDGET_ID = 'telegram-discussion'
const SCRIPT_ID = 'telegram-widget-script'

const mountWidget = () => {
  const container = document.getElementById(WIDGET_ID)
  if (!container) return
  container.innerHTML = ''

  const script = document.createElement('script')
  script.id = SCRIPT_ID
  script.src = 'https://telegram.org/js/telegram-widget.js?22'
  script.async = true
  script.setAttribute('data-telegram-discussion', 'psmocommunity/4')
  script.setAttribute('data-comments-limit', '50')
  script.setAttribute('data-color', '#ff8a4c')
  script.setAttribute('data-dark-color', '#f05365')
  script.setAttribute('data-colorful', '1')
  container.appendChild(script)
}

const removeWidget = () => {
  document.getElementById(SCRIPT_ID)?.remove()
  const container = document.getElementById(WIDGET_ID)
  if (container) container.innerHTML = ''
}

const reloadWidget = () => {
  removeWidget()
  setTimeout(mountWidget, 50)
}

const scrollToWidget = () => {
  document.getElementById('chat-widget')?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(() => {
  mountWidget()
})

onBeforeUnmount(() => {
  removeWidget()
})
</script>

<style scoped>
.chat-view {
  background: linear-gradient(180deg, #f8fafc 0%, #eef2ff 45%, #ffffff 100%);
  min-height: calc(100vh - 60px);
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  color: #1d2433;
}

.hero {
  border-radius: 24px;
  padding: 1.8rem;
  color: #fff;
  background: radial-gradient(circle at right top, rgba(255, 255, 255, 0.25), transparent 55%),
    linear-gradient(135deg, #ff9d53 0%, #f05365 100%);
  box-shadow: 0 24px 60px rgba(240, 83, 101, 0.25);
}

.hero-copy h1 {
  margin: 0.4rem 0;
  font-size: clamp(1.6rem, 4vw, 2rem);
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.25rem 0.85rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.25);
  font-size: 0.8rem;
}

.summary {
  line-height: 1.55;
  margin: 0.5rem 0 1.2rem;
  max-width: 540px;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.cta {
  border-radius: 999px;
  padding: 0.65rem 1.4rem;
  font-weight: 600;
  text-decoration: none;
  text-align: center;
  transition: opacity 0.2s ease;
}

.cta.primary {
  background: white;
  color: #f05365;
}

.cta.ghost {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.35);
}

.cta:hover {
  opacity: 0.9;
}

.hero-metrics {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-top: 1.2rem;
}

.hero-metrics span {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.35rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 0.8rem;
}

.status-card {
  background: white;
  border-radius: 18px;
  padding: 1.2rem;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.08);
}

.status-card .label {
  font-size: 0.78rem;
  color: #7a808f;
  margin: 0;
}

.status-card .value {
  margin: 0.3rem 0 0;
  font-size: 1.15rem;
  font-weight: 700;
  color: #1d2433;
}

.status-card .value.online {
  color: #1ec7a6;
}

.status-card .desc {
  margin: 0.5rem 0 0;
  font-size: 0.85rem;
  color: #5f667a;
}

.widget-wrapper {
  background: white;
  border-radius: 24px;
  padding: 1rem;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.07);
}

.widget-wrapper header {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  align-items: center;
  margin-bottom: 0.75rem;
}

.widget-wrapper h2 {
  margin: 0;
  font-size: 1.15rem;
}

.widget-wrapper p {
  margin: 0;
  font-size: 0.82rem;
  color: #7a808f;
}

.refresh-btn {
  border: 1px solid #d3d9e6;
  background: transparent;
  border-radius: 999px;
  padding: 0.4rem 1.1rem;
  font-weight: 600;
  color: #1d2433;
  cursor: pointer;
}

.widget-shell {
  background: #f8f9fb;
  border-radius: 14px;
  min-height: 420px;
  padding: 0.6rem;
}

:deep(iframe) {
  width: 100% !important;
  border-radius: 12px;
}

.loading-text {
  text-align: center;
  padding-top: 2rem;
  color: #7a808f;
}

.guide-card,
.app-section {
  background: white;
  border-radius: 24px;
  padding: 1.5rem;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.07);
}

.guide-card h3 {
  margin: 0 0 0.7rem;
}

.guide-card ol {
  margin: 0;
  padding-left: 1.2rem;
  color: #5f667a;
  line-height: 1.6;
  font-size: 0.92rem;
}

.app-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
  align-items: center;
}

.promo-copy h3 {
  margin: 0 0 0.35rem;
}

.promo-copy p {
  margin: 0 0 1rem;
  color: #5f667a;
}

.download-buttons {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.download {
  flex: 1;
  min-width: 120px;
  text-align: center;
  border-radius: 12px;
  padding: 0.75rem;
  text-decoration: none;
  font-weight: 600;
  color: white;
}

.download.android {
  background: #18c964;
}

.download.ios {
  background: #007aff;
}

.promo-visual {
  font-size: 3rem;
  text-align: center;
}

@media (max-width: 768px) {
  .hero {
    padding: 1.2rem;
  }
  .status-card {
    padding: 1rem;
  }
  .widget-wrapper header {
    flex-direction: column;
    align-items: flex-start;
  }
  .refresh-btn {
    width: 100%;
    text-align: center;
  }
}
</style>
