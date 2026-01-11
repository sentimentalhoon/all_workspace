import { fileURLToPath, URL } from 'node:url'

import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { defineConfig } from 'vite'
import { VitePWA } from 'vite-plugin-pwa'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig(() => {
  const backendPort = process.env.VITE_BACKEND_PORT || '8080'
  const backendHost = process.env.VITE_BACKEND_HOST || 'localhost'

  return {
    plugins: [
      vue(),
      vueJsx(),
      vueDevTools(),
      VitePWA({
        registerType: 'autoUpdate',
        workbox: {
          cleanupOutdatedCaches: true,
        },
        manifest: {
          id: '/?source=pwa',
          name: 'PSMO Community',
          short_name: 'PSMO',
          description: '캠핑 스테이션 & PSMO 커뮤니티를 한 번에 사용하는 모바일 퍼스트 경험',
          lang: 'ko',
          start_url: '/',
          scope: '/',
          display: 'standalone',
          display_override: ['standalone', 'minimal-ui'],
          orientation: 'portrait',
          background_color: '#0f172a',
          theme_color: '#ff8a4c',
          categories: ['social', 'productivity', 'lifestyle'],
          icons: [
            {
              src: '/icons/pwa-192.svg',
              sizes: '192x192',
              type: 'image/svg+xml',
              purpose: 'any',
            },
            {
              src: '/icons/pwa-512.svg',
              sizes: '512x512',
              type: 'image/svg+xml',
              purpose: 'any maskable',
            },
          ],
          screenshots: [
            {
              src: '/screenshot-mobile.png',
              sizes: '540x960',
              type: 'image/png',
              form_factor: 'narrow',
            },
          ],
          shortcuts: [
            { name: '게시판 바로가기', url: '/board', description: '인기 캠핑 게시판' },
            { name: '채팅 열기', url: '/chat', description: '실시간 커뮤니티 채팅' },
          ],
        },
      }),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      host: '0.0.0.0',
      port: 3000,
      // Proxy for API requests in dev server
      proxy: {
        '/api': {
          target: `http://${backendHost}:${backendPort}`,
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path,
        },
      },
      watch: {
        usePolling: true,
        interval: 1000,
      },
      hmr: {
        host: 'localhost',
        protocol: 'ws',
      },
    },
  }
})
