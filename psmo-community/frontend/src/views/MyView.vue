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

      <FooterLinks :links="footerLinks" />
      <AppInfo v-bind="appInfo" />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview 내 정보 페이지. 프로필, 통계, 메뉴 섹션을 한 화면에서 제공합니다.
 */
import AppInfo from '@/views/my/components/AppInfo.vue'
import FooterLinks from '@/views/my/components/FooterLinks.vue'
import MenuSection from '@/views/my/components/MenuSection.vue'
import ProfileSection from '@/views/my/components/ProfileSection.vue'
import StatsGrid from '@/views/my/components/StatsGrid.vue'
import { useMyProfile } from '@/views/my/composables/useMyProfile'
import { appInfo, footerLinks, menuSections, userStats } from '@/views/my/constants'

/**
 * 인증 상태, 아바타, 텔레그램 로그인 흐름 등을 포함한 프로필 인터랙션 훅입니다.
 * @see useMyProfile
 */
const {
  user,
  isAuthenticated,
  loading,
  avatarUrl,
  errorMessage,
  loginPending,
  telegramBotUsername,
  handleTelegramLogin,
  handleLogout,
  refreshProfile,
} = useMyProfile()

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
