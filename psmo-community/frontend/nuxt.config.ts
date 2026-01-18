// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2024-04-03",

  // 개발자 도구를 켭니다 (F12 말고, 화면 하단에 나오는 Nuxt 전용 도구)
  devtools: { enabled: true },

  // 사용할 모듈(플러그인) 목록입니다.
  // @pinia/nuxt: 상태 관리(전역 변수) 도구인 Pinia를 사용합니다.
  modules: ["@pinia/nuxt"],

  // 개발 서버 설정
  devServer: {
    port: 3000,
  },

  // API Proxy 설정 (CORS 및 개발 편의성)
  routeRules: {
    "/api/**": {
      proxy: "http://localhost:8080/api/**",
    },
  },

  // 니트로(Nitro) 서버 설정
  nitro: {
    // devProxy는 routeRules로 대체됨
  },

  css: [
    // '~/assets/main.scss' // 나중에 공통 CSS 파일을 만들면 주석을 풀어서 씁니다.
  ],
});
