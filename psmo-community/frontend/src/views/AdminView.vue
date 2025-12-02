<script setup lang="ts">
/**
 * @fileoverview 관리자 점검 페이지. DB 테스트와 헬스체크 위젯을 모아 보여줍니다.
 */
import DatabaseTest from '@/components/DatabaseTest.vue'
import HealthCheck from '@/components/HealthCheck.vue'
import AdminHeader from '@/views/admin/components/AdminHeader.vue'
import AdminSection from '@/views/admin/components/AdminSection.vue'
</script>

<template>
  <main class="admin">
    <AdminHeader />

    <AdminSection title="데이터베이스 테스트">
      <DatabaseTest />
    </AdminSection>

    <AdminSection title="시스템 상태">
      <HealthCheck />
    </AdminSection>
  </main>
</template>

<style src="@/views/admin/styles/admin-view.css"></style>
