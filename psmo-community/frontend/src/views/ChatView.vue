<template>
  <main class="chat-view">
    <section class="chat-header">
      <p class="badge">Live · WebSocket & Telegram</p>
      <h1>PSMO 커뮤니티 채팅 허브</h1>
      <p class="subtext">
        텔레그램 공개 채널과 JWT 로 보호되는 실시간 웹소켓 채팅을 한 화면에서 전환하며 사용할 수
        있습니다.
      </p>
      <div class="header-actions">
        <button class="cta" type="button" @click="focusRealtime">웹소켓 채팅 시작</button>
        <button class="cta ghost" type="button" @click="focusTelegram">텔레그램 보기</button>
      </div>
      <ul class="status-chips">
        <li>웹소켓 · Redis Broadcast</li>
        <li>Telegram 연동</li>
        <li>모바일 최적화</li>
      </ul>
      <a class="channel-link" href="https://t.me/psmocommunity" target="_blank" rel="noreferrer">
        Telegram 채널 새 창에서 열기 →
      </a>
    </section>

    <section class="chat-tabs" :id="tabsSectionId">
      <div class="tab-controls" role="tablist" aria-label="채팅 모드 선택">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          class="tab-button"
          type="button"
          role="tab"
          :aria-selected="activeTab === tab.id"
          :class="{ active: activeTab === tab.id }"
          @click="setActiveTab(tab.id)"
        >
          <span class="tab-label">{{ tab.label }}</span>
          <span class="tab-description">{{ tab.description }}</span>
        </button>
      </div>

      <div class="tab-panels">
        <RealtimeChatPanel
          v-if="activeTab === 'realtime'"
          :is-authenticated="isAuthenticated"
          :user="user"
          :login-pending="loginPending"
          :login-error="authErrorMessage"
          @login="handleTelegramLogin"
        />
        <section v-else class="chat-widget" :id="widgetSectionId">
          <header>
            <div>
              <p class="widget-tag">@psmocommunity</p>
              <h2>Telegram 공개 채팅</h2>
            </div>
            <button type="button" class="refresh" @click="reloadWidget">새로고침</button>
          </header>
          <div :id="widgetContainerId" class="widget-frame">
            <p class="loading-text">채팅 위젯을 불러오는 중입니다...</p>
          </div>
        </section>
      </div>
    </section>

    <section class="chat-help">
      <h3>이용 가이드</h3>
      <ol>
        <li>웹소켓 채팅은 텔레그램 로그인 후 자동으로 연결되며 최대 2000자까지 지원합니다.</li>
        <li>Telegram 탭은 누구나 읽을 수 있지만 작성은 Telegram 계정 연동이 필요합니다.</li>
        <li>욕설·광고·개인정보 유출은 즉시 제재되며 로그가 영구 저장됩니다.</li>
        <li>개인 거래·동행은 반드시 DM 으로만 진행해 주세요.</li>
      </ol>
    </section>
  </main>
</template>

<script setup lang="ts">
/**
 * @fileoverview 커뮤니티 채팅 페이지. 텔레그램 위젯과 자체 웹소켓 채팅을 탭으로 전환합니다.
 */
import { useTelegramAuth } from '@/composables/useTelegramAuth'
import { nextTick, ref, watch } from 'vue'
import RealtimeChatPanel from './chat/RealtimeChatPanel.vue'
import { useTelegramWidget } from './chat/useTelegramWidget'

const tabs = [
  { id: 'realtime', label: '실시간 커뮤니티', description: '웹소켓 · JWT 보호' },
  { id: 'telegram', label: 'Telegram', description: '공식 공개 채널' },
] as const

type TabId = (typeof tabs)[number]['id']

const tabsSectionId = 'chat-modes'
const activeTab = ref<TabId>('realtime')

const { widgetContainerId, widgetSectionId, reloadWidget, scrollToWidget, mountWidget } =
  useTelegramWidget()

const {
  user,
  isAuthenticated,
  errorMessage: authErrorMessage,
  loginPending,
  handleTelegramLogin,
} = useTelegramAuth()

const scrollToTabs = () => {
  document.getElementById(tabsSectionId)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const setActiveTab = async (tab: TabId, options: { focus?: boolean } = {}) => {
  activeTab.value = tab
  await nextTick()
  if (tab === 'telegram') {
    if (options.focus) {
      scrollToWidget()
    }
  } else if (options.focus) {
    scrollToTabs()
  }
}

const focusRealtime = () => {
  void setActiveTab('realtime', { focus: true })
}

const focusTelegram = () => {
  void setActiveTab('telegram', { focus: true })
}

watch(
  () => activeTab.value,
  (tab) => {
    if (tab === 'telegram') {
      void nextTick(() => mountWidget())
    }
  },
  { immediate: true },
)
</script>

<style src="./chat/chat-view.css"></style>
