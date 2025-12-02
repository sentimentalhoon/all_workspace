<template>
  <div class="chat-view">
    <ChatHero
      :badge="heroContent.badge"
      :title="heroContent.title"
      :summary="heroContent.summary"
      :primary-cta="heroContent.primaryCta"
      :secondary-cta-label="heroContent.secondaryCtaLabel"
      :metrics="heroContent.metrics"
      @scroll-to-widget="scrollToWidget"
    />

    <ChatStatusGrid :cards="statusCards" />

    <ChatWidget
      :title="widgetContent.title"
      :subtitle="widgetContent.subtitle"
      :container-id="widgetContainerId"
      :section-id="widgetSectionId"
      :reload-label="widgetContent.reloadLabel"
      :loading-text="widgetContent.loadingText"
      @reload="reloadWidget"
    />

    <ChatGuide :title="guideContent.title" :steps="guideContent.steps" />

    <ChatAppPromo
      :title="appPromoContent.title"
      :description="appPromoContent.description"
      :android-link="appPromoContent.androidLink"
      :ios-link="appPromoContent.iosLink"
      :visual="appPromoContent.visual"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview 커뮤니티 채팅 페이지로, 텔레그램 위젯과 안내 섹션들을 구성합니다.
 */
import ChatAppPromo from './chat/components/ChatAppPromo.vue'
import ChatGuide from './chat/components/ChatGuide.vue'
import ChatHero from './chat/components/ChatHero.vue'
import ChatStatusGrid, { type StatusCard } from './chat/components/ChatStatusGrid.vue'
import ChatWidget from './chat/components/ChatWidget.vue'
import { useTelegramWidget } from './chat/useTelegramWidget'

/**
 * 텔레그램 디스커션 위젯의 DOM 식별자와 제어 핸들러를 제공합니다.
 * @see useTelegramWidget
 */
const { widgetContainerId, widgetSectionId, reloadWidget, scrollToWidget } = useTelegramWidget()

/**
 * 히어로 섹션에 노출할 문구와 CTA 구성을 정의합니다.
 * @type {{
 *   badge: string;
 *   title: string;
 *   summary: string;
 *   primaryCta: { label: string; href: string };
 *   secondaryCtaLabel: string;
 *   metrics: string[];
 * }}
 */
const heroContent = {
  badge: 'Live · Telegram',
  title: 'PSMO Community Chat',
  summary:
    '실시간 대화와 현장 소식을 놓치지 마세요. 텔레그램 위젯을 통해 모바일에서도 쾌적하게 채팅할 수 있도록 최적화했습니다.',
  primaryCta: {
    label: '채널 바로가기',
    href: 'https://t.me/psmocommunity',
  },
  secondaryCtaLabel: '채팅 영역으로 이동',
  metrics: ['24/7 Live', '알림 지원', '모바일 최적화'],
}

/**
 * 주요 운영 상태를 요약하는 카드 데이터입니다.
 * @type {StatusCard[]}
 */
const statusCards: StatusCard[] = [
  {
    label: '현재 상태',
    value: 'Online',
    valueClass: 'online',
    description: '운영팀이 실시간으로 모니터링하며 안전한 공간을 유지합니다.',
  },
  {
    label: '인기 주제',
    value: '동행 구하기 · 캠핑팁 · 중고거래',
  },
  {
    label: '필수 조건',
    value: '텔레그램 계정 + PSMO 가입',
  },
]

/**
 * 텔레그램 위젯 제목/문구와 로딩 메시지를 정의합니다.
 * @type {{ title: string; subtitle: string; reloadLabel: string; loadingText: string }}
 */
const widgetContent = {
  title: '커뮤니티 채팅',
  subtitle: '@psmocommunity · Telegram Discussion Widget',
  reloadLabel: '새로고침',
  loadingText: '채팅 위젯을 불러오는 중입니다...',
}

/**
 * 이용 가이드 섹션에서 보여줄 주요 단계를 정리합니다.
 * @type {{ title: string; steps: string[] }}
 */
const guideContent = {
  title: '빠른 입장 가이드',
  steps: [
    '위젯 하단에서 <strong>댓글 작성</strong>을 눌러 로그인합니다.',
    '처음이라면 텔레그램 앱 설치 후 계정을 연결합니다.',
    '운영 가이드(광고 금지, 개인정보 보호)를 준수해 주세요.',
    '개인 거래/동행은 DM으로만 진행해 주세요.',
  ],
}

/**
 * 앱 설치 프로모션 블록에 전달할 데이터입니다.
 * @type {{ title: string; description: string; androidLink: string; iosLink: string; visual: string }}
 */
const appPromoContent = {
  title: '앱에서도 더 빠르게',
  description: '텔레그램 앱을 설치하면 알림과 다중 디바이스 동기화가 더욱 편리해집니다.',
  androidLink: 'https://play.google.com/store/apps/details?id=org.telegram.messenger',
  iosLink: 'https://apps.apple.com/app/telegram-messenger/id686449807',
  visual: '📱',
}
</script>

<style src="./chat/chat-view.css"></style>
