import type { ChatServerMessage } from '@/types/chat'
import { getAccessToken } from '@/utils/api'
import { computed, onBeforeUnmount, ref, watch } from 'vue'

type ConnectionStatus =
  | 'idle'
  | 'connecting'
  | 'connected'
  | 'reconnecting'
  | 'error'
  | 'unauthorized'

type UseRealtimeChatOptions = {
  maxMessages?: number
}

const DEFAULT_MAX_MESSAGES = 120

const CLOSE_CODE_POLICY_VIOLATION = 1008

const getWsUrl = (token: string): string | null => {
  if (typeof window === 'undefined') {
    return null
  }

  const explicit = import.meta.env.VITE_CHAT_WS_URL
  if (explicit) {
    const url = new URL(explicit)
    url.searchParams.set('token', token)
    return url.toString()
  }

  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'

  if (import.meta.env.DEV) {
    const host = import.meta.env.VITE_BACKEND_HOST ?? window.location.hostname
    const port = import.meta.env.VITE_BACKEND_PORT ?? '8081'
    const authority = port ? `${host}:${port}` : host
    const url = new URL(`${protocol}//${authority}/ws/chat`)
    url.searchParams.set('token', token)
    return url.toString()
  }

  const url = new URL(`${protocol}//${window.location.host}/ws/chat`)
  url.searchParams.set('token', token)
  return url.toString()
}

export function useRealtimeChat(options?: UseRealtimeChatOptions) {
  const wsRef = ref<WebSocket | null>(null)
  const connectionStatus = ref<ConnectionStatus>('idle')
  const lastError = ref<string | null>(null)
  const messages = ref<ChatServerMessage[]>([])
  const shouldMaintainConnection = ref(false)
  const reconnectTimer = ref<number | null>(null)
  const reconnectAttempts = ref(0)
  let restartRequested = false

  const maxMessages = options?.maxMessages ?? DEFAULT_MAX_MESSAGES

  const clearReconnectTimer = () => {
    if (reconnectTimer.value) {
      window.clearTimeout(reconnectTimer.value)
      reconnectTimer.value = null
    }
  }

  const scheduleReconnect = () => {
    if (!shouldMaintainConnection.value) {
      return
    }
    clearReconnectTimer()
    reconnectAttempts.value += 1
    const delay = Math.min(1000 * 2 ** reconnectAttempts.value, 10_000)
    reconnectTimer.value = window.setTimeout(() => {
      connectionStatus.value = 'reconnecting'
      establishConnection()
    }, delay)
  }

  const handleMessage = (event: MessageEvent<string>) => {
    try {
      const payload = JSON.parse(event.data) as ChatServerMessage
      if (!payload?.messageId || !payload.content) {
        return
      }

      const existingIndex = messages.value.findIndex((msg) => msg.messageId === payload.messageId)
      if (existingIndex >= 0) {
        messages.value[existingIndex] = payload
      } else {
        messages.value.push(payload)
        if (messages.value.length > maxMessages) {
          messages.value.splice(0, messages.value.length - maxMessages)
        }
      }
    } catch (error) {
      lastError.value = error instanceof Error ? error.message : '메시지를 해석할 수 없습니다.'
    }
  }

  const handleClose = (event: CloseEvent) => {
    wsRef.value = null
    clearReconnectTimer()

    if (restartRequested) {
      restartRequested = false
      establishConnection()
      return
    }

    if (!shouldMaintainConnection.value) {
      connectionStatus.value = 'idle'
      return
    }

    if (!getAccessToken()) {
      connectionStatus.value = 'unauthorized'
      return
    }

    if (event.code === CLOSE_CODE_POLICY_VIOLATION) {
      return
    }

    lastError.value = event.reason || '연결이 종료되었습니다. 잠시 후 다시 시도합니다.'
    connectionStatus.value = 'error'
    scheduleReconnect()
  }

  const handleError = () => {
    lastError.value = '네트워크 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.'
  }

  const establishConnection = () => {
    if (!shouldMaintainConnection.value || wsRef.value) {
      return
    }

    const accessToken = getAccessToken()
    if (!accessToken) {
      connectionStatus.value = 'unauthorized'
      return
    }

    const endpoint = getWsUrl(accessToken)
    if (!endpoint) {
      connectionStatus.value = 'error'
      lastError.value = '웹소켓 URL을 구성할 수 없습니다.'
      return
    }

    try {
      connectionStatus.value = reconnectAttempts.value > 0 ? 'reconnecting' : 'connecting'
      const socket = new WebSocket(endpoint)
      wsRef.value = socket

      socket.addEventListener('message', handleMessage)
      socket.addEventListener('close', handleClose)
      socket.addEventListener('error', handleError)
      socket.addEventListener('open', () => {
        connectionStatus.value = 'connected'
        reconnectAttempts.value = 0
        lastError.value = null
      })
    } catch (error) {
      wsRef.value = null
      connectionStatus.value = 'error'
      lastError.value = error instanceof Error ? error.message : '연결을 생성하지 못했습니다.'
      scheduleReconnect()
    }
  }

  const connect = () => {
    shouldMaintainConnection.value = true
    reconnectAttempts.value = 0
    restartRequested = false
    establishConnection()
  }

  const disconnect = () => {
    shouldMaintainConnection.value = false
    restartRequested = false
    clearReconnectTimer()
    if (wsRef.value) {
      wsRef.value.close(1000, 'Client closed')
      wsRef.value = null
    }
    messages.value = []
    lastError.value = null
    connectionStatus.value = 'idle'
  }

  const restartConnection = () => {
    if (!shouldMaintainConnection.value) {
      return
    }
    restartRequested = true
    clearReconnectTimer()
    if (wsRef.value) {
      wsRef.value.close(1000, 'Restarting connection')
      return
    }
    establishConnection()
  }

  const sendMessage = (content: string) => {
    if (!wsRef.value || wsRef.value.readyState !== WebSocket.OPEN) {
      throw new Error('연결되지 않았습니다.')
    }
    const trimmed = content.trim()
    if (!trimmed) {
      throw new Error('빈 메시지는 전송할 수 없습니다.')
    }
    wsRef.value.send(
      JSON.stringify({
        type: 'chat_message',
        content: trimmed,
      }),
    )
  }

  watch(
    () => getAccessToken(),
    (accessToken, previousToken) => {
      if (!shouldMaintainConnection.value) {
        return
      }
      if (!accessToken) {
        disconnect()
        connectionStatus.value = 'unauthorized'
        return
      }
      if (accessToken !== previousToken) {
        restartConnection()
      }
    },
  )

  onBeforeUnmount(() => {
    disconnect()
  })

  const isConnected = computed(() => connectionStatus.value === 'connected')

  return {
    messages,
    connectionStatus,
    isConnected,
    lastError,
    connect,
    disconnect,
    sendMessage,
  }
}
