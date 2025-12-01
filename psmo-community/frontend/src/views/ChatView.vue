<template>
  <div class="chat-view">
    <div class="page-header">
      <h2>💬 커뮤니티 채팅</h2>
      <p class="header-desc">텔레그램 연동 실시간 채팅방</p>
    </div>

    <div class="chat-container">
      <div class="chat-notice">
        <div class="notice-icon">📱</div>
        <h3>텔레그램과 연동된 채팅방</h3>
        <p>웹과 텔레그램 앱 양쪽에서 사용 가능합니다</p>
        <ul>
          <li>텔레그램 계정으로 로그인 필요</li>
          <li>실시간 알림 수신 가능</li>
          <li>피씨방 업주/관리자 전용</li>
        </ul>
      </div>

      <div class="telegram-widget-container">
        <div id="telegram-discussion"></div>
      </div>

      <div class="chat-info">
        <h3>💡 사용 방법</h3>
        <ol>
          <li>
            <strong>텔레그램 채널 참여:</strong>
            <a 
              href="https://t.me/psmocommunity" 
              target="_blank" 
              class="telegram-link"
            >
              @psmocommunity
            </a>
          </li>
          <li><strong>아래 채팅창에서 "댓글 작성" 클릭</strong></li>
          <li><strong>텔레그램 계정으로 로그인</strong> (처음 한 번만)</li>
          <li><strong>메시지 작성 및 전송</strong></li>
        </ol>
      </div>

      <div class="telegram-app-promo">
        <div class="promo-content">
          <div class="promo-icon">📲</div>
          <div class="promo-text">
            <h4>텔레그램 앱 다운로드</h4>
            <p>모바일에서 푸시 알림을 받으세요</p>
          </div>
        </div>
        <div class="download-buttons">
          <a 
            href="https://play.google.com/store/apps/details?id=org.telegram.messenger" 
            target="_blank"
            class="download-btn android"
          >
            Android
          </a>
          <a 
            href="https://apps.apple.com/app/telegram-messenger/id686449807" 
            target="_blank"
            class="download-btn ios"
          >
            iOS
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  // Telegram Widget 스크립트 동적 로드
  const script = document.createElement('script')
  script.src = 'https://telegram.org/js/telegram-widget.js?22'
  script.async = true
  script.setAttribute('data-telegram-discussion', 'psmocommunity/3')
  script.setAttribute('data-comments-limit', '50')
  script.setAttribute('data-colorful', '1')
  script.setAttribute('data-color', '667EEA')
  script.setAttribute('data-dark-color', '764BA2')
  
  const container = document.getElementById('telegram-discussion')
  if (container) {
    container.appendChild(script)
  }
})
</script>

<style scoped>
.chat-view {
  background: #f8f9fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  background: white;
  padding: 1.5rem 1rem;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.page-header h2 {
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
  color: #2d3748;
}

.header-desc {
  margin: 0;
  font-size: 0.85rem;
  color: #666;
}

.chat-container {
  padding: 1rem;
}

.chat-notice {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  text-align: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.notice-icon {
  font-size: 3rem;
  margin-bottom: 0.5rem;
}

.chat-notice h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.2rem;
}

.chat-notice p {
  margin: 0 0 1rem 0;
  font-size: 0.9rem;
  opacity: 0.9;
}

.chat-notice ul {
  list-style: none;
  padding: 0;
  margin: 0;
  text-align: left;
  max-width: 300px;
  margin: 0 auto;
}

.chat-notice ul li {
  padding: 0.5rem 0;
  font-size: 0.85rem;
  opacity: 0.9;
}

.chat-notice ul li::before {
  content: '✓ ';
  margin-right: 0.5rem;
}

.telegram-widget-container {
  background: white;
  border-radius: 12px;
  padding: 1rem;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  min-height: 400px;
}

.chat-info {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chat-info h3 {
  margin: 0 0 1rem 0;
  font-size: 1.1rem;
  color: #2d3748;
}

.chat-info ol {
  margin: 0;
  padding-left: 1.5rem;
  color: #666;
}

.chat-info ol li {
  margin-bottom: 0.75rem;
  line-height: 1.6;
  font-size: 0.9rem;
}

.telegram-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  border-bottom: 2px solid #667eea;
  transition: all 0.2s;
}

.telegram-link:hover {
  color: #5568d3;
  border-bottom-color: #5568d3;
}

.telegram-app-promo {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.promo-content {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.promo-icon {
  font-size: 3rem;
}

.promo-text h4 {
  margin: 0 0 0.25rem 0;
  font-size: 1.1rem;
  color: #2d3748;
}

.promo-text p {
  margin: 0;
  font-size: 0.85rem;
  color: #666;
}

.download-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.download-btn {
  display: block;
  padding: 0.75rem;
  text-align: center;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.2s;
}

.download-btn.android {
  background: #3ddc84;
  color: white;
}

.download-btn.android:hover {
  background: #2bc870;
  transform: translateY(-2px);
}

.download-btn.ios {
  background: #007aff;
  color: white;
}

.download-btn.ios:hover {
  background: #0062cc;
  transform: translateY(-2px);
}

/* Telegram Widget 스타일 오버라이드 */
:deep(iframe) {
  border-radius: 8px;
  width: 100% !important;
}
</style>
