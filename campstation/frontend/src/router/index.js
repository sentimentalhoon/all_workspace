import { useAuthStore } from "@/stores/auth";
import About from "@/views/About.vue";
import Home from "@/views/Home.vue";
import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
    meta: { title: "홈" },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "로그인", guest: true },
  },
  {
    path: "/search",
    name: "Search",
    component: () => import("@/views/Search.vue"),
    meta: { title: "검색" },
  },
  {
    path: "/my",
    name: "My",
    component: () => import("@/views/My.vue"),
    meta: { title: "MY", requiresAuth: true },
  },
  {
    path: "/about",
    name: "About",
    component: About,
    meta: { title: "정보" },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navigation guard - 인증이 필요한 페이지 보호
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  // 인증이 필요한 페이지
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: "Login", query: { redirect: to.fullPath } });
  }
  // 로그인한 사용자가 로그인 페이지 접근 시 홈으로
  else if (to.meta.guest && authStore.isAuthenticated) {
    next({ name: "Home" });
  }
  // 그 외의 경우 정상 진행
  else {
    next();
  }
});

export default router;
