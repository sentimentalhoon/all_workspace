/// <reference types="vite/client" />
/// <reference types="vite-plugin-pwa/client" />

import type { TelegramLoginPayload } from './src/types/auth'

interface ImportMetaEnv {
  readonly VITE_TELEGRAM_BOT_ID?: string
  readonly VITE_TELEGRAM_BOT_USERNAME?: string
  readonly VITE_BACKEND_HOST?: string
  readonly VITE_BACKEND_PORT?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare global {
  interface TelegramLoginOptions {
    bot_id: number
    request_access?: 'read' | 'write'
  }

  interface TelegramLoginSDK {
    auth(options: TelegramLoginOptions, callback: (user: TelegramLoginPayload | null) => void): void
  }

  interface TelegramGlobal {
    Login?: TelegramLoginSDK
  }

  interface Window {
    Telegram?: TelegramGlobal
  }
}

export {}
