/**
 * 전역 인증 미들웨어.
 * 모든 페이지 이동 시마다 실행됩니다.
 * 로그인이 필요한 페이지에 들어가려고 하면 로그인 페이지로 쫓아냅니다.
 */
export default defineNuxtRouteMiddleware((to, from) => {
  // 로그인 없이 볼 수 있는 페이지 목록 (프리픽스 매칭)
  const publicRoutes = ["/login", "/about", "/error", "/market", "/community"];

  // 1. 정확히 매칭되는 경로 (메인 페이지 등)
  if (to.path === "/") return;

  // 2. publicRoutes 로 시작하는 경로 (예: /market/1, /community/notice)
  //    또는 페이지 자체적으로 auth: false 메타데이터를 가진 경우
  const isPublicPath = publicRoutes.some((path) => to.path.startsWith(path));

  if (isPublicPath || to.meta.auth === false) {
    return; // 그냥 통과
  }

  const authStore = useAuthStore();

  // 로그인 안 했으면 로그인 페이지로 이동 (/login)
  if (!authStore.isLoggedIn) {
    // 참고: SSR(서버 사이드 렌더링) 환경에서는 쿠키를 확인해야 안전하지만,
    // Pinia 상태(isLoggedIn)가 잘 초기화되었다면 이걸로도 충분합니다.
    return navigateTo("/login");
  }
});
