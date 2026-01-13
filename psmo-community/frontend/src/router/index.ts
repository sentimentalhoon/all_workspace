import { useAuthStore } from '@/stores/auth'
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true },
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/report',
      name: 'report',
      component: () => import('../views/ReportView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/market',
      name: 'market',
      component: () => import('../views/market/MarketListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/market/create',
      name: 'market-create',
      component: () => import('../views/market/MarketCreateView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/market/:id',
      name: 'market-detail',
      component: () => import('../views/market/MarketDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/board',
      name: 'board',
      component: () => import('../views/BoardView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/my',
      name: 'my',
      component: () => import('../views/MyView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false },
    },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  // routes without explicit auth requirement are allowed (e.g., /login)
  if (to.meta.requiresAuth === false) {
    return true
  }

  // already authenticated
  if (auth.isAuthenticated) {
    return true
  }

  // try to recover session (silent)
  try {
    await auth.checkAuth()
  } catch {
    // ignore
  }

  if (auth.isAuthenticated) {
    return true
  }

  return {
    name: 'login',
    query: { redirect: to.fullPath },
  }
})

export default router
