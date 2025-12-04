<template>
  <section class="realtime-panel">
    <header class="panel-header">
      <div>
        <p class="widget-tag beta">웹소켓 베타</p>
        <h2>커뮤니티 실시간 채팅</h2>
      </div>
      <span class="connection-indicator" :class="statusClass">{{ statusLabel }}</span>
    </header>

    <p class="panel-subtitle">
      텔레그램 인증 한 번이면 커뮤니티 전용 채팅방에 바로 입장할 수 있습니다. 메시지는 JWT 토큰으로
      보호된 웹소켓을 통해 전송됩니다.
    </p>

    <div ref="messageListRef" class="chat-log">
      <ul v-if="formattedMessages.length" class="chat-log__list">
        <li
          v-for="message in formattedMessages"
          :key="message.messageId"
          class="chat-message"
          :class="{ 'is-self': currentUserId === message.userId }"
        >
          <div class="chat-message__meta">
            <span class="chat-message__author">{{ message.author }}</span>
            <span class="chat-message__time">{{ message.timestamp }}</span>
          </div>
          <p class="chat-message__content">{{ message.content }}</p>
        </li>
      </ul>
      <p v-else class="empty-state">{{ emptyStateText }}</p>
    </div>

    <div v-if="isAuthenticated" class="chat-composer">
      <textarea
        v-model="draft"
        class="chat-input"
        rows="2"
        placeholder="메시지를 입력하고 Enter 로 전송하세요 (Shift + Enter 줄바꿈)"
        @keydown="handleComposerKeydown"
      ></textarea>
      <button type="button" class="send-btn" :disabled="!canSend" @click="handleSubmit">
        전송
      </button>
    </div>

    <div v-else class="chat-login-hint">
      <p>
        텔레그램 인증을 완료하면 바로 참여할 수 있습니다. <RouterLink to="/my">내 정보</RouterLink>
        페이지에서도 로그인 상태를 확인할 수 있어요.
      </p>
      <button type="button" class="cta" :disabled="loginPending" @click="emit('login')">
        {{ loginPending ? '로그인 중...' : '텔레그램 인증하기' }}
      </button>
      <p v-if="loginError" class="chat-login-hint__error">{{ loginError }}</p>
    </div>

    <p v-if="connectionError" class="connection-error">{{ connectionError }}</p>
  </section>
</template>

<script setup lang="ts">
import type { UserResponse } from '@/types/auth'
import { getAccessToken, isTokenExpiringSoon, refreshAccessToken } from '@/utils/api'
import { useRealtimeChat } from '@/views/chat/useRealtimeChat'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'

const props = defineProps<{
  isAuthenticated: boolean
  user: UserResponse | null
  loginPending: boolean
  loginError: string | null
}>()

const emit = defineEmits<{
  (event: 'login'): void
}>()

const { messages, connectionStatus, lastError, connect, disconnect, sendMessage } = useRealtimeChat(
  {
    maxMessages: 200,
  },
)

const draft = ref('')
const messageListRef = ref<HTMLDivElement | null>(null)
const localError = ref<string | null>(null)
let tokenCheckTimer: number | undefined

const checkTokenPeriodically = async () => {
  if (!props.isAuthenticated) return
  const token = getAccessToken()
  if (token && isTokenExpiringSoon(token)) {
    console.log('[Chat] Token expiring soon, refreshing...')
    await refreshAccessToken()
  }
}

onMounted(() => {
  // 1분마다 토큰 만료 여부 확인
  tokenCheckTimer = window.setInterval(checkTokenPeriodically, 60 * 1000)
})

onBeforeUnmount(() => {
  if (tokenCheckTimer) {
    clearInterval(tokenCheckTimer)
  }
})

const currentUserId = computed(() => props.user?.id ?? null)

const statusLabel = computed(() => {
  switch (connectionStatus.value) {
    case 'connected':
      return '연결됨'
    case 'connecting':
      return '연결 중'
    case 'reconnecting':
      return '재연결 중'
    case 'unauthorized':
      return '인증 필요'
    case 'error':
      return '오류'
    default:
      return '대기'
  }
})

const statusClass = computed(() => {
  switch (connectionStatus.value) {
    case 'connected':
      return 'is-online'
    case 'connecting':
    case 'reconnecting':
      return 'is-pending'
    case 'error':
    case 'unauthorized':
      return 'is-offline'
    default:
      return 'is-idle'
  }
})

const timestampFormatter = new Intl.DateTimeFormat('ko-KR', {
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
})

const formattedMessages = computed(() =>
  messages.value.map((message) => ({
    ...message,
    author: message.displayName || message.username || '익명 사용자',
    timestamp: timestampFormatter.format(new Date(message.createdAtEpochMillis)),
  })),
)

const emptyStateText = computed(() => {
  if (!props.isAuthenticated) {
    return '텔레그램 로그인 후 최근 대화를 바로 확인할 수 있습니다.'
  }
  if (connectionStatus.value === 'connecting' || connectionStatus.value === 'reconnecting') {
    return '채팅 서버에 연결하는 중입니다...'
  }
  if (connectionStatus.value === 'unauthorized') {
    return '세션이 만료되었습니다. 다시 로그인해 주세요.'
  }
  if (connectionStatus.value === 'error') {
    return '연결이 불안정합니다. 잠시 후 다시 시도해 주세요.'
  }
  return '아직 메시지가 없습니다. 첫 대화를 시작해 보세요!'
})

const connectionError = computed(() => localError.value ?? lastError.value)

const canSend = computed(
  () =>
    props.isAuthenticated &&
    connectionStatus.value === 'connected' &&
    draft.value.trim().length > 0,
)

const handleSubmit = () => {
  if (!canSend.value) {
    return
  }
  try {
    sendMessage(draft.value)
    draft.value = ''
    localError.value = null
  } catch (error) {
    localError.value = error instanceof Error ? error.message : '메시지를 전송할 수 없습니다.'
  }
}

const handleComposerKeydown = (event: KeyboardEvent) => {
  if (event.key !== 'Enter') {
    return
  }
  if (event.shiftKey) {
    return
  }
  event.preventDefault()
  handleSubmit()
}

watch(
  () => props.isAuthenticated,
  (isAuthed) => {
    if (isAuthed) {
      connect()
    } else {
      disconnect()
    }
  },
  { immediate: true },
)

watch(
  () => messages.value.length,
  async () => {
    await nextTick()
    const container = messageListRef.value
    if (container) {
      container.scrollTop = container.scrollHeight
    }
  },
)

watch(connectionStatus, () => {
  if (connectionStatus.value !== 'error') {
    localError.value = null
  }
})
</script>

<style scoped>
.realtime-panel {
  background: #fff;
  border-radius: 24px;
  padding: 1.5rem;
  box-shadow: 0 12px 40px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: center;
}

.widget-tag.beta {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.78rem;
  text-transform: uppercase;
  color: #f05365;
}

.connection-indicator {
  border-radius: 999px;
  padding: 0.25rem 0.9rem;
  font-size: 0.8rem;
  font-weight: 600;
  border: 1px solid transparent;
}

.connection-indicator.is-online {
  background: rgba(52, 199, 89, 0.15);
  color: #1e7b3b;
  border-color: rgba(52, 199, 89, 0.2);
}

.connection-indicator.is-pending {
  background: rgba(255, 184, 0, 0.15);
  color: #9a5a00;
  border-color: rgba(255, 184, 0, 0.4);
}

.connection-indicator.is-offline {
  background: rgba(240, 83, 101, 0.12);
  color: #b02a3c;
  border-color: rgba(240, 83, 101, 0.3);
}

.connection-indicator.is-idle {
  background: rgba(15, 23, 42, 0.05);
  color: #1d2433;
  border-color: rgba(15, 23, 42, 0.12);
}

.panel-subtitle {
  margin: 0;
  color: #3a4157;
  line-height: 1.5;
}

.chat-log {
  background: #f8f9fb;
  border-radius: 16px;
  padding: 1rem;
  min-height: 360px;
  max-height: 520px;
  overflow-y: auto;
}

.chat-log__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
}

.chat-message {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  background: #fff;
  border-radius: 16px;
  padding: 0.75rem 1rem;
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.chat-message.is-self {
  align-self: flex-end;
  background: rgba(255, 138, 76, 0.1);
  border-color: rgba(255, 138, 76, 0.2);
}

.chat-message__meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.78rem;
  color: #4f566b;
}

.chat-message__author {
  font-weight: 600;
  color: #1d2433;
}

.chat-message__content {
  margin: 0;
  word-break: break-word;
  line-height: 1.5;
  color: #1d2433;
}

.empty-state {
  text-align: center;
  color: #4f566b;
  padding: 3rem 0;
  margin: 0;
}

.chat-composer {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.chat-input {
  width: 100%;
  border-radius: 16px;
  border: 1px solid #d3d9e6;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  resize: none;
  background: #fff;
}

.send-btn {
  align-self: flex-end;
  border-radius: 999px;
  border: none;
  padding: 0.6rem 1.5rem;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #f05365, #ff9d53);
  cursor: pointer;
}

.send-btn:disabled {
  background: #d3d9e6;
  cursor: not-allowed;
}

.chat-login-hint {
  background: rgba(15, 23, 42, 0.04);
  border-radius: 16px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  color: #1d2433;
}

.chat-login-hint__error {
  color: #c53030;
  font-size: 0.9rem;
  margin: 0;
}

.connection-error {
  margin: 0;
  color: #b02a3c;
  font-size: 0.9rem;
}

@media (min-width: 768px) {
  .realtime-panel {
    padding: 2rem;
  }

  .chat-composer {
    flex-direction: row;
    align-items: flex-end;
  }

  .chat-input {
    flex: 1;
  }
}
</style>
