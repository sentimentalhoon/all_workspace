export default defineNuxtRouteMiddleware((to, from) => {
  // Whitelist public routes
  const publicRoutes = ["/login", "/about", "/error"];
  if (publicRoutes.includes(to.path) || to.meta.auth === false) {
    return;
  }

  const authStore = useAuthStore();

  // Client-side guard driven by store state
  if (!authStore.isLoggedIn) {
    // We could try to refresh profile here if cookie exists but state is empty
    // (handled in app.vue or plugin usually, but simple check here)
    // For strict SSR protection, checking cookie directly on server side is better.
    // But store state should be hydration-ready if we use Nuxt correctly.

    // Simplified: redirect to login
    return navigateTo("/login");
  }
});
