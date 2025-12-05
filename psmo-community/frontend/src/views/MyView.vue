<template>
  <div class="my-view">
    <div class="page-header">
      <h2>내 정보</h2>
    </div>

    <div class="my-container">
      <ProfileSection
        :is-authenticated="isAuthenticated"
        :user="user"
        :avatar-url="avatarUrl"
        :loading="loading"
        :login-pending="loginPending"
        :error-message="errorMessage"
        :telegram-bot-username="telegramBotUsername"
        :role="role"
        :score="score"
        :activity-level="activityLevel"
        :next-level-info="nextLevelInfo"
        @login="handleTelegramLogin"
        @refresh="refreshProfile"
        @logout="handleLogout"
      />

      <StatsGrid :stats="userStats" />

      <MenuSection
        v-for="section in menuSections"
        :key="section.title"
        :title="section.title"
        :items="section.items"
        @select="handleMenuSelect"
      />

      <div
        class="debug-info"
        style="
          margin-top: 20px;
          padding: 10px;
          background: #f5f5f5;
          border-radius: 8px;
          font-size: 12px;
          word-break: break-all;
        "
      >
        <h3>토큰 정보 (디버깅용)</h3>
        <p><strong>Access Token:</strong> {{ accessTokenDisplay }}</p>
        <p><strong>남은 시간:</strong> {{ remainingTime }}</p>
      </div>

      <FooterLinks :links="footerLinks" />
      <AppInfo v-bind="appInfo" />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview 내 정보 페이지. 프로필, 통계, 메뉴 섹션을 한 화면에서 제공합니다.
 */
import { getAccessToken } from '@/utils/api'
import AppInfo from '@/views/my/components/AppInfo.vue'
import FooterLinks from '@/views/my/components/FooterLinks.vue'
import MenuSection from '@/views/my/components/MenuSection.vue'
import ProfileSection from '@/views/my/components/ProfileSection.vue'
import StatsGrid from '@/views/my/components/StatsGrid.vue'
import { useMyProfile } from '@/views/my/composables/useMyProfile'
import { appInfo, footerLinks, menuSections, userStats } from '@/views/my/constants'
import { onMounted, onUnmounted, ref } from 'vue'

/**
 * 인증 상태, 아바타, 텔레그램 로그인 흐름 등을 포함한 프로필 인터랙션 훅입니다.
 * @see useMyProfile
 */
const {
  user,
  isAuthenticated,
  loading,
  avatarUrl,
  role,
  score,
  activityLevel,
  nextLevelInfo,
  errorMessage,
  loginPending,
  telegramBotUsername,
  handleTelegramLogin,
  handleLogout,
  refreshProfile,
} = useMyProfile()

const accessTokenDisplay = ref('')
const remainingTime = ref('')
let timer: number | undefined

const updateTokenInfo = () => {
  const token = getAccessToken()
  accessTokenDisplay.value = token || '없음'

  if (token) {
    try {
      const parts = token.split('.')
      const payloadPart = parts[1]
      if (payloadPart) {
        const payload = JSON.parse(atob(payloadPart.replace(/-/g, '+').replace(/_/g, '/')))
        if (payload.exp) {
          const now = Math.floor(Date.now() / 1000)
          const diff = payload.exp - now
          if (diff > 0) {
            const minutes = Math.floor(diff / 60)
            const seconds = diff % 60
            remainingTime.value = `${minutes}분 ${seconds}초`
          } else {
            remainingTime.value = '만료됨'
          }
        }
      }
    } catch (e) {
      remainingTime.value = '파싱 오류'
    }
  } else {
    remainingTime.value = '-'
  }
}

onMounted(() => {
  updateTokenInfo()
  timer = window.setInterval(updateTokenInfo, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

/**
 * 메뉴 항목 선택 시 후속 동작을 바인딩합니다.
 * @param {string} action 선택된 메뉴의 식별자
 * @returns {void}
 */
const handleMenuSelect = (action: string) => {
  console.info('Menu item selected (placeholder)', action)
}
</script>

<style src="@/views/my/styles/my-view.css"></style>
