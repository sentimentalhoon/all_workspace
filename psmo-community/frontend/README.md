# PSMO

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd) 
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

## Telegram Login Configuration

The "내 정보" 화면은 텔레그램 로그인 위젯을 사용해 백엔드 `/api/auth/telegram`과 `/api/me`를 연동합니다.

1. `frontend` 디렉터리에 `.env.local` 파일을 만들고 아래 항목을 추가하세요:

   ```dotenv
   VITE_TELEGRAM_BOT_ID=8578829111
   VITE_TELEGRAM_BOT_USERNAME=Psmo_community_bot
   ```

   (운영 환경에서는 본인의 봇 ID/사용자명으로 변경)

2. `npm run dev`로 개발 서버를 실행하면 **내 정보** 화면에서 “텔레그램으로 로그인” 버튼이 나타납니다. 버튼을 누르면 텔레그램 로그인 팝업 → 백엔드 토큰 교환 → `/api/me` 프로필 조회 순서로 진행됩니다.

3. 발급된 JWT와 사용자 정보는 `localStorage`(`psmo-auth-session`)에 저장됩니다. 로그아웃 버튼을 누르면 세션이 초기화됩니다.

> Docker 환경(dev/prod 컨테이너)에서는 `.env.local` 대신 `VITE_TELEGRAM_BOT_ID`와 `VITE_TELEGRAM_BOT_USERNAME`을 환경 변수나 build args로 주입하면 동일하게 동작합니다.

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```

### Run Unit Tests with [Vitest](https://vitest.dev/)

```sh
npm run test:unit
```

### Run End-to-End Tests with [Playwright](https://playwright.dev)

```sh
# Install browsers for the first run
npx playwright install

# When testing on CI, must build the project first
npm run build

# Runs the end-to-end tests
npm run test:e2e
# Runs the tests only on Chromium
npm run test:e2e -- --project=chromium
# Runs the tests of a specific file
npm run test:e2e -- tests/example.spec.ts
# Runs the tests in debug mode
npm run test:e2e -- --debug
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```

## PWA (Progressive Web App)

- `vite-plugin-pwa` is enabled so every `npm run build` emits an installable bundle (manifest + service worker).
- Use `npm run build && npm run preview` or deploy to HTTPS to verify the “Add to Home Screen” prompt.
- Update the branded icons under `public/icons/pwa-*.svg` if you change the visual identity.

