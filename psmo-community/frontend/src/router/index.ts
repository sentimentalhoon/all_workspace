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
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
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
      path: '/chat',
      name: 'chat',
      component: () => import('../views/ChatView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/games',
      name: 'games',
      component: () => import('../views/GamesView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/games/snail',
      name: 'games-snail',
      component: () => import('../views/games/SnailRaceView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/games/blackjack',
      name: 'games-blackjack',
      component: () => import('../views/games/BlackjackView.vue'),
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
