<script setup lang="ts">
import { useAuthStore } from '@/stores/auth' // Assuming existing auth store
import { useQuery } from '@tanstack/vue-query'
import axios from 'axios'
import QrcodeVue from 'qrcode.vue'
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const authStore = useAuthStore()

const isChecking = ref(true)
const loginSuccess = ref(false)

// 1. Init Session (Get UUID & DeepLink)
const {
  data: initData,
  isError: isInitError,
  refetch: refetchInit,
} = useQuery({
  queryKey: ['qr-init'],
  queryFn: async () => {
    const res = await axios.post('/api/auth/qr/init')
    return res.data // { uuid, deepLink, expiresIn }
  },
  staleTime: 0, // Always fetch fresh session on mount
  refetchOnWindowFocus: false,
})

// 2. Poll Status
// We only enable polling when we have a UUID and haven't succeeded yet.
const { data: statusData, error: statusError } = useQuery({
  queryKey: ['qr-check', () => initData.value?.uuid],
  queryFn: async () => {
    if (!initData.value?.uuid) return null
    const res = await axios.get('/api/auth/qr/check', {
      params: { uuid: initData.value.uuid },
      validateStatus: (status) => status < 500, // Allow 404
    })
    return res.data // { status: 'pending' | 'verified' | undefined (expired/404) }
  },
  enabled: () => !!initData.value?.uuid && !loginSuccess.value,
  refetchInterval: (query) => {
    const data = query.state.data
    // If verified or expired (404/undefined), stop polling
    if (loginSuccess.value || (data && data.status !== 'pending')) return false
    return 2000 // Poll every 2s
  },
  refetchOnWindowFocus: true,
})

// 3. Watch for "verified" status and Claim
watch(statusData, async (newVal) => {
  if (newVal?.status === 'verified' && !loginSuccess.value) {
    loginSuccess.value = true
    try {
      // Claim the session
      const claimRes = await axios.post('/api/auth/qr/claim', {
        uuid: initData.value?.uuid,
      })

      const { user, token } = claimRes.data

      // Update Client State
      authStore.setAuth(user, token.accessToken) // Assuming setAuth actions exist

      // Redirect
      router.push('/')
    } catch (e) {
      console.error('Claim failed', e)
      loginSuccess.value = false // Resume polling if claim failed? Or show error
      alert('로그인 처리 중 오류가 발생했습니다. 다시 시도해주세요.')
    }
  } else if (statusError.value || (newVal && !newVal.status)) {
    // Session expired or 404
    // Ideally we should show "Expired" and button to refresh
  }
})

const handleRetry = () => {
  loginSuccess.value = false
  refetchInit()
}
</script>

<template>
  <div class="qr-login-container">
    <div v-if="initData" class="qr-wrapper">
      <h3>QR 코드로 로그인</h3>
      <p>휴대폰 카메라나 텔레그램으로 스캔하세요</p>

      <div class="qr-box">
        <QrcodeVue :value="initData.deepLink" :size="200" level="H" />
      </div>

      <div class="status-message">
        <span v-if="loginSuccess" class="success">로그인 성공! 이동 중...</span>
        <span v-else-if="statusData?.status === 'pending'" class="pending">스캔 대기 중...</span>
        <span
          v-else-if="statusError || (statusData && statusData.status !== 'verified')"
          class="error"
        >
          유효 시간이 만료되었습니다.
          <button @click="handleRetry">새로고침</button>
        </span>
        <span v-else>세션 생성 중...</span>
      </div>

      <div class="mobile-link">
        <a :href="initData.deepLink" target="_blank">텔레그램 앱 실행하기 (모바일)</a>
      </div>
    </div>

    <div v-else-if="isInitError" class="error-state">
      <p>QR 코드 생성 실패</p>
      <button @click="handleRetry">다시 시도</button>
    </div>

    <div v-else class="loading">Loading...</div>
  </div>
</template>

<style scoped>
.qr-login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  background: #1e293b;
  border-radius: 1rem;
  color: white;
  text-align: center;
}

.qr-box {
  margin: 1.5rem 0;
  padding: 1rem;
  background: white;
  border-radius: 0.5rem;
}

.status-message {
  margin-top: 1rem;
  min-height: 1.5rem;
  font-weight: bold;
}

.status-message .success {
  color: #4ade80;
}
.status-message .pending {
  color: #94a3b8;
}
.status-message .error {
  color: #f87171;
}

.mobile-link {
  margin-top: 2rem;
  font-size: 0.9rem;
}
.mobile-link a {
  color: #60a5fa;
  text-decoration: none;
}
</style>
