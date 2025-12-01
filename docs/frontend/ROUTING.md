# Vue Router 가이드

## 📋 개요

Vue Router는 Vue.js의 공식 라우팅 라이브러리로, SPA(Single Page Application)에서 페이지 전환을 관리합니다.

**버전**: Vue Router 4.5.0 (Vue 3용)

## 🏗️ 기본 설정

### 라우터 설정 파일 (`src/router/index.js`)

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import About from '../views/About.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home
  },
  {
    path: '/about',
    name: 'about',
    component: About
  },
  {
    // 동적 라우트 매칭
    path: '/user/:id',
    name: 'user',
    component: () => import('../views/User.vue'),
    props: true  // URL 파라미터를 props로 전달
  },
  {
    // 중첩 라우트
    path: '/dashboard',
    component: () => import('../views/Dashboard.vue'),
    children: [
      {
        path: '',
        name: 'dashboard-home',
        component: () => import('../views/DashboardHome.vue')
      },
      {
        path: 'profile',
        name: 'dashboard-profile',
        component: () => import('../views/DashboardProfile.vue')
      }
    ]
  },
  {
    // 404 페이지
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

export default router
```

### 앱에 라우터 등록 (`src/main.js`)

```javascript
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(router)
app.mount('#app')
```

### 루트 컴포넌트 (`src/App.vue`)

```vue
<template>
  <div id="app">
    <nav>
      <router-link to="/">Home</router-link>
      <router-link to="/about">About</router-link>
    </nav>
    
    <!-- 라우트 컴포넌트가 렌더링되는 곳 -->
    <router-view />
  </div>
</template>
```

## 🔗 네비게이션

### router-link 사용

```vue
<template>
  <!-- 기본 사용 -->
  <router-link to="/">Home</router-link>
  
  <!-- Named Route -->
  <router-link :to="{ name: 'user', params: { id: 123 }}">
    User 123
  </router-link>
  
  <!-- 쿼리 파라미터 -->
  <router-link :to="{ path: '/search', query: { q: 'vue' }}">
    Search
  </router-link>
  
  <!-- 커스텀 태그 -->
  <router-link to="/about" custom v-slot="{ navigate, href }">
    <a :href="href" @click="navigate">About</a>
  </router-link>
  
  <!-- Active 클래스 -->
  <router-link 
    to="/about" 
    active-class="active"
    exact-active-class="exact-active"
  >
    About
  </router-link>
</template>

<style>
.active {
  color: #42b983;
}
.exact-active {
  font-weight: bold;
}
</style>
```

### 프로그래매틱 네비게이션

```vue
<script setup>
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 페이지 이동
function goToHome() {
  router.push('/')
}

function goToUser(id) {
  router.push({ name: 'user', params: { id } })
}

function goToSearch(query) {
  router.push({ path: '/search', query: { q: query } })
}

// 뒤로 가기
function goBack() {
  router.back()
}

// 앞으로 가기
function goForward() {
  router.forward()
}

// 특정 단계로 이동
function go(n) {
  router.go(n)  // -1: 뒤로, 1: 앞으로
}

// 현재 경로 교체 (히스토리에 추가하지 않음)
function replaceRoute() {
  router.replace('/login')
}

// 현재 라우트 정보 접근
console.log(route.path)      // '/user/123'
console.log(route.params)    // { id: '123' }
console.log(route.query)     // { tab: 'profile' }
console.log(route.name)      // 'user'
console.log(route.fullPath)  // '/user/123?tab=profile'
</script>

<template>
  <div>
    <button @click="goToHome">Home</button>
    <button @click="goToUser(456)">User 456</button>
    <button @click="goBack">뒤로</button>
  </div>
</template>
```

## 🎯 동적 라우트 매칭

### URL 파라미터

```javascript
// router/index.js
{
  path: '/user/:id',
  component: User
}
```

```vue
<!-- User.vue -->
<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()

// URL 파라미터 접근
console.log(route.params.id)
</script>

<template>
  <div>User ID: {{ $route.params.id }}</div>
</template>
```

### Props로 파라미터 전달 (권장)

```javascript
// router/index.js
{
  path: '/user/:id',
  component: User,
  props: true  // params를 props로 전달
}

// 객체 모드
{
  path: '/user/:id',
  component: User,
  props: { newsletter: true }
}

// 함수 모드
{
  path: '/user/:id',
  component: User,
  props: route => ({ 
    id: route.params.id,
    query: route.query.q
  })
}
```

```vue
<!-- User.vue -->
<script setup>
const props = defineProps({
  id: String
})
</script>

<template>
  <div>User ID: {{ id }}</div>
</template>
```

### 여러 파라미터

```javascript
// /user/john/post/123
{
  path: '/user/:username/post/:postId',
  component: UserPost
}
```

### 선택적 파라미터

```javascript
// /user 또는 /user/123 모두 매칭
{
  path: '/user/:id?',
  component: User
}
```

### 정규식 파라미터

```javascript
// 숫자만 허용
{
  path: '/user/:id(\\d+)',
  component: User
}

// 여러 세그먼트
{
  path: '/:chapters+',  // 하나 이상
  component: Chapters
}

{
  path: '/:chapters*',  // 0개 이상
  component: Chapters
}
```

## 🌳 중첩 라우트

```javascript
// router/index.js
{
  path: '/user/:id',
  component: User,
  children: [
    {
      // /user/:id 일 때 매칭
      path: '',
      component: UserHome
    },
    {
      // /user/:id/profile 일 때 매칭
      path: 'profile',
      component: UserProfile
    },
    {
      // /user/:id/posts 일 때 매칭
      path: 'posts',
      component: UserPosts
    }
  ]
}
```

```vue
<!-- User.vue -->
<template>
  <div class="user">
    <h2>User {{ $route.params.id }}</h2>
    <nav>
      <router-link :to="`/user/${$route.params.id}`">Home</router-link>
      <router-link :to="`/user/${$route.params.id}/profile`">Profile</router-link>
      <router-link :to="`/user/${$route.params.id}/posts`">Posts</router-link>
    </nav>
    
    <!-- 중첩된 라우트 렌더링 -->
    <router-view />
  </div>
</template>
```

## 🛡️ 네비게이션 가드

### 글로벌 가드

```javascript
// router/index.js
import { createRouter } from 'vue-router'

const router = createRouter({ /* ... */ })

// 모든 네비게이션 전에 실행
router.beforeEach((to, from, next) => {
  // 인증 체크
  const isAuthenticated = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    // 로그인 페이지로 리다이렉트
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else {
    next()  // 계속 진행
  }
})

// 모든 네비게이션 후에 실행
router.afterEach((to, from) => {
  // 페이지 타이틀 변경
  document.title = to.meta.title || 'My App'
  
  // 분석 추적
  console.log(`Navigated from ${from.path} to ${to.path}`)
})
```

### 라우트별 가드

```javascript
{
  path: '/admin',
  component: Admin,
  beforeEnter: (to, from, next) => {
    const user = getCurrentUser()
    
    if (user && user.isAdmin) {
      next()
    } else {
      next('/unauthorized')
    }
  },
  // 여러 가드
  beforeEnter: [checkAuth, checkRole]
}
```

### 컴포넌트 내 가드

```vue
<script setup>
import { onBeforeRouteLeave, onBeforeRouteUpdate } from 'vue-router'

// 라우트를 떠나기 전
onBeforeRouteLeave((to, from) => {
  const answer = window.confirm('정말 나가시겠습니까?')
  if (!answer) return false  // 네비게이션 취소
})

// 같은 컴포넌트 내에서 라우트 변경 시
onBeforeRouteUpdate((to, from) => {
  // 예: /user/1 -> /user/2
  console.log('Route updated:', to.params.id)
})
</script>
```

## 🏷️ Named Routes & Views

### Named Routes

```javascript
{
  path: '/user/:id',
  name: 'user',
  component: User
}
```

```vue
<template>
  <router-link :to="{ name: 'user', params: { id: 123 }}">
    User 123
  </router-link>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

router.push({ name: 'user', params: { id: 123 }})
</script>
```

### Named Views

여러 뷰를 동시에 렌더링:

```javascript
{
  path: '/',
  components: {
    default: Home,
    sidebar: Sidebar,
    footer: Footer
  }
}
```

```vue
<template>
  <router-view />              <!-- default -->
  <router-view name="sidebar" />
  <router-view name="footer" />
</template>
```

## 📄 메타 필드

```javascript
{
  path: '/admin',
  component: Admin,
  meta: {
    requiresAuth: true,
    title: 'Admin Panel',
    roles: ['admin'],
    layout: 'dashboard'
  }
}
```

```javascript
// 가드에서 사용
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    // 인증 체크
  }
  
  // 페이지 타이틀
  document.title = to.meta.title || 'Default Title'
  
  next()
})
```

```vue
<!-- 컴포넌트에서 사용 -->
<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
const layout = route.meta.layout
</script>
```

## 🔄 리다이렉트 & 별칭

### 리다이렉트

```javascript
// 문자열
{
  path: '/home',
  redirect: '/'
}

// Named Route
{
  path: '/home',
  redirect: { name: 'homepage' }
}

// 함수
{
  path: '/search/:searchText',
  redirect: to => {
    return { path: '/search', query: { q: to.params.searchText } }
  }
}

// 상대 경로
{
  path: '/users/:id/posts',
  redirect: to => 'profile'  // /users/:id/profile
}
```

### 별칭 (Alias)

```javascript
{
  path: '/home',
  component: Home,
  alias: '/'  // '/'로 접근해도 Home 표시
}

// 여러 별칭
{
  path: '/users',
  component: Users,
  alias: ['/people', '/folks']
}
```

## 🎨 트랜지션

### 기본 트랜지션

```vue
<template>
  <router-view v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
```

### 라우트별 트랜지션

```vue
<template>
  <router-view v-slot="{ Component, route }">
    <transition :name="route.meta.transition || 'fade'">
      <component :is="Component" />
    </transition>
  </router-view>
</template>
```

```javascript
{
  path: '/about',
  component: About,
  meta: { transition: 'slide' }
}
```

## 📱 히스토리 모드

### HTML5 History Mode (권장)

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes
})
```

**URL**: `https://example.com/user/123`

**서버 설정 필요** (Nginx):
```nginx
location / {
  try_files $uri $uri/ /index.html;
}
```

### Hash Mode

```javascript
import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes
})
```

**URL**: `https://example.com/#/user/123`

서버 설정 불필요

## 📜 스크롤 동작

```javascript
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      // 뒤로가기 버튼 시 이전 위치로
      return savedPosition
    } else if (to.hash) {
      // 해시 앵커로 스크롤
      return { el: to.hash, behavior: 'smooth' }
    } else {
      // 페이지 상단으로
      return { top: 0 }
    }
  }
})
```

### 지연된 스크롤

```javascript
scrollBehavior(to, from, savedPosition) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ top: 0 })
    }, 300)
  })
}
```

## 🔧 고급 패턴

### Lazy Loading

```javascript
// 라우트별 코드 스플리팅
{
  path: '/about',
  component: () => import('../views/About.vue')
}

// 같은 청크로 그룹화
{
  path: '/user/:id',
  component: () => import(/* webpackChunkName: "user" */ '../views/User.vue')
}
```

### 라우트 매칭 우선순위

```javascript
// 더 구체적인 라우트를 먼저 정의
const routes = [
  { path: '/user/create', component: UserCreate },
  { path: '/user/:id', component: User },  // 위에 있으면 create가 id로 인식됨
]
```

### 동적 라우트 추가

```javascript
// 런타임에 라우트 추가
router.addRoute({
  path: '/dynamic',
  component: DynamicComponent
})

// 중첩 라우트 추가
router.addRoute('parent', {
  path: 'child',
  component: Child
})

// 라우트 제거
const removeRoute = router.addRoute(routeRecord)
removeRoute()  // 추가된 라우트 제거
```

## 🧪 테스팅

```javascript
import { mount } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'

const router = createRouter({
  history: createMemoryHistory(),
  routes: [/* ... */]
})

const wrapper = mount(Component, {
  global: {
    plugins: [router]
  }
})

// 라우트 네비게이션
await router.push('/about')
await router.isReady()
```

## 📚 실전 예제

### 인증 라우팅

```javascript
// router/index.js
const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'dashboard-home',
        component: () => import('../views/DashboardHome.vue')
      }
    ]
  }
]

router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.meta.guest && isAuthenticated) {
    next({ name: 'dashboard-home' })
  } else {
    next()
  }
})
```

### 권한 기반 라우팅

```javascript
router.beforeEach((to, from, next) => {
  const user = getCurrentUser()
  const requiredRoles = to.meta.roles
  
  if (requiredRoles && user) {
    const hasRole = requiredRoles.some(role => user.roles.includes(role))
    
    if (hasRole) {
      next()
    } else {
      next('/forbidden')
    }
  } else {
    next()
  }
})
```

## 🔗 참고 자료

- [Vue Router 공식 문서](https://router.vuejs.org/)
- [Vue Router Migration Guide (v3→v4)](https://router.vuejs.org/guide/migration/)
