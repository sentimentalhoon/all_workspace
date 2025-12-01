import { fileURLToPath, URL } from 'node:url'

import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { defineConfig } from 'vite'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const isDev = mode === 'development'
  const backendPort = process.env.VITE_BACKEND_PORT || '8080'
  const backendHost = process.env.VITE_BACKEND_HOST || 'localhost'

  return {
    plugins: [vue(), vueJsx(), vueDevTools()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      host: '0.0.0.0',
      port: 3000,
      // Proxy only in development mode
      ...(isDev && {
        proxy: {
          '/api': {
            target: `http://${backendHost}:${backendPort}`,
            changeOrigin: true,
            secure: false,
          },
        },
      }),
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
