<template>
  <main class="chat-view">
    <section class="chat-header">
      <p class="badge">Live · Telegram</p>
      <h1>PSMO Community Chat</h1>
      <p class="subtext">
        모바일 우선 UI에서 바로 텔레그램 채널과 대화하세요. 채팅 영역은 실시간으로 새로 고쳐집니다.
      </p>
      <div class="header-actions">
        <a class="cta" href="https://t.me/psmocommunity" target="_blank" rel="noreferrer">
          채널 열기
        </a>
        <button class="cta ghost" type="button" @click="scrollToWidget">채팅으로 이동</button>
      </div>
      <ul class="status-chips">
        <li>24/7 온라인</li>
        <li>알림 지원</li>
        <li>모바일 최적화</li>
      </ul>
    </section>

    <section class="chat-widget" :id="widgetSectionId">
      <header>
        <div>
          <p class="widget-tag">@psmocommunity</p>
          <h2>실시간 채팅</h2>
        </div>
        <button type="button" class="refresh" @click="reloadWidget">새로고침</button>
      </header>
      <div :id="widgetContainerId" class="widget-frame">
        <p class="loading-text">채팅 위젯을 불러오는 중입니다...</p>
      </div>
    </section>

    <section class="chat-help">
      <h3>간단 가이드</h3>
      <ol>
        <li>하단 <strong>댓글 작성</strong> 버튼으로 텔레그램 로그인</li>
        <li>처음이라면 앱 설치 후 동일 계정으로 연동</li>
        <li>광고/개인정보 유출 금지, 신고 시 즉시 제재</li>
        <li>개인 거래·동행은 DM 으로만 진행</li>
      </ol>
    </section>
  </main>
</template>

<script setup lang="ts">
/**
 * @fileoverview 모바일 친화적인 채팅 전용 페이지. 텔레그램 위젯과 최소한의 가이드만 제공합니다.
 */
import { useTelegramWidget } from './chat/useTelegramWidget'

/**
 * 텔레그램 위젯 컨테이너 ID 및 제어 핸들러.
 * @see useTelegramWidget
 */
const { widgetContainerId, widgetSectionId, reloadWidget, scrollToWidget } = useTelegramWidget()
</script>

<style src="./chat/chat-view.css"></style>
