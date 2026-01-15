// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2024-04-03",
  devtools: { enabled: true },
  modules: ["@pinia/nuxt"],
  devServer: {
    port: 3000,
  },
  nitro: {
    devProxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
  css: [
    // '~/assets/main.scss' // Will create this later
  ],
});
