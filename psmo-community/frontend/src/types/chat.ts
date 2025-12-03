export type ChatMessageType = 'history' | 'chat_message'

export interface ChatServerMessage {
  type: ChatMessageType
  messageId: number
  userId: number
  displayName?: string | null
  username?: string | null
  content: string
  createdAtEpochMillis: number
}

export interface ChatOutboundMessage {
  type: 'chat_message'
  content: string
}
