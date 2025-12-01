# Vue.js 3 프론트엔드 기초 가이드

## 📋 개요

Campstation과 PSMO Community 프로젝트 모두 동일한 기술 스택을 사용합니다:
- **Vue.js 3.5.13** - Progressive JavaScript Framework
- **Vite 6.0.3** - 빠른 개발 서버 및 빌드 도구
- **Vue Router 4.5.0** - SPA 라우팅
- **Pinia 2.3.0** - 상태 관리
- **Axios 1.7.9** - HTTP 클라이언트

## 🏗️ 프로젝트 구조

```
frontend/
├── public/              # 정적 파일 (favicon 등)
│   └── favicon.ico
├── src/
│   ├── assets/         # 이미지, 폰트, 스타일
│   ├── components/     # 재사용 가능한 컴포넌트
│   ├── views/          # 페이지 컴포넌트
│   ├── router/         # 라우터 설정
│   │   └── index.js
│   ├── stores/         # Pinia 스토어 (상태 관리)
│   ├── services/       # API 서비스
│   ├── utils/          # 유틸리티 함수
│   ├── App.vue         # 루트 컴포넌트
│   └── main.js         # 앱 엔트리 포인트
├── index.html          # HTML 템플릿
├── package.json        # 의존성 관리
├── vite.config.js      # Vite 설정
└── Dockerfile          # Docker 이미지 빌드
```

## 📦 주요 의존성

### 핵심 라이브러리
```json
{
  "vue": "^3.5.13",           // Vue.js 프레임워크
  "vue-router": "^4.5.0",     // 라우팅
  "pinia": "^2.3.0",          // 상태 관리
  "axios": "^1.7.9"           // HTTP 요청
}
```

### 개발 도구
```json
{
  "@vitejs/plugin-vue": "^5.2.1",  // Vite Vue 플러그인
  "vite": "^6.0.3"                  // 빌드 도구
}
```

## 🚀 개발 시작하기

### 1. 의존성 설치
```bash
# Campstation
cd campstation/frontend
npm install

# PSMO Community
cd psmo-community/frontend
npm install
```

### 2. 개발 서버 실행
```bash
# Campstation (포트 3000)
npm run dev

# PSMO Community (포트 3001)
npm run dev
```

### 3. 프로덕션 빌드
```bash
npm run build
```

### 4. 빌드 미리보기
```bash
npm run preview
```

## 📝 Vue.js 3 핵심 개념

### 1. Composition API

Vue 3의 새로운 방식으로, 로직을 재사용 가능하게 구성합니다.

```vue
<script setup>
import { ref, computed, onMounted } from 'vue'

// 반응형 상태
const count = ref(0)
const message = ref('Hello Vue!')

// 계산된 속성
const doubleCount = computed(() => count.value * 2)

// 메서드
function increment() {
  count.value++
}

// 라이프사이클 훅
onMounted(() => {
  console.log('컴포넌트가 마운트되었습니다')
})
</script>

<template>
  <div>
    <p>{{ message }}</p>
    <p>Count: {{ count }}</p>
    <p>Double: {{ doubleCount }}</p>
    <button @click="increment">증가</button>
  </div>
</template>

<style scoped>
/* 컴포넌트 전용 스타일 */
button {
  padding: 8px 16px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
```

### 2. 반응형 상태 (Reactivity)

#### ref - 기본 타입용
```javascript
import { ref } from 'vue'

const count = ref(0)
const name = ref('John')

// 값 접근 시 .value 사용
console.log(count.value)  // 0
count.value++             // 1
```

#### reactive - 객체용
```javascript
import { reactive } from 'vue'

const state = reactive({
  user: {
    name: 'John',
    age: 30
  },
  items: []
})

// 직접 접근 가능
console.log(state.user.name)
state.user.age = 31
```

#### computed - 계산된 속성
```javascript
import { ref, computed } from 'vue'

const firstName = ref('John')
const lastName = ref('Doe')

const fullName = computed(() => {
  return `${firstName.value} ${lastName.value}`
})
```

#### watch - 상태 감시
```javascript
import { ref, watch } from 'vue'

const count = ref(0)

watch(count, (newValue, oldValue) => {
  console.log(`Count changed from ${oldValue} to ${newValue}`)
})

// 여러 값 감시
watch([firstName, lastName], ([newFirst, newLast]) => {
  console.log(`Name: ${newFirst} ${newLast}`)
})
```

### 3. 컴포넌트 기본 구조

```vue
<script setup>
// Import 문
import { ref, computed, onMounted } from 'vue'
import ChildComponent from './ChildComponent.vue'

// Props 정의
const props = defineProps({
  title: String,
  count: {
    type: Number,
    default: 0
  }
})

// Emits 정의
const emit = defineEmits(['update', 'delete'])

// 상태
const localCount = ref(props.count)

// 메서드
function handleUpdate() {
  emit('update', localCount.value)
}

// 라이프사이클
onMounted(() => {
  console.log('Component mounted')
})
</script>

<template>
  <div class="container">
    <h2>{{ title }}</h2>
    <p>Count: {{ localCount }}</p>
    <button @click="handleUpdate">Update</button>
    <ChildComponent />
  </div>
</template>

<style scoped>
.container {
  padding: 20px;
}
</style>
```

### 4. 템플릿 문법

#### 텍스트 보간
```vue
<template>
  <p>{{ message }}</p>
  <p>{{ count * 2 }}</p>
  <p>{{ isActive ? 'Yes' : 'No' }}</p>
</template>
```

#### 디렉티브

**v-bind (속성 바인딩)**
```vue
<template>
  <img :src="imageUrl" :alt="imageAlt">
  <div :class="{ active: isActive }">
  <a :href="url">Link</a>
</template>
```

**v-on (이벤트 처리)**
```vue
<template>
  <button @click="handleClick">Click</button>
  <input @input="handleInput" @keyup.enter="submit">
  <form @submit.prevent="onSubmit">
</template>
```

**v-if / v-else-if / v-else (조건부 렌더링)**
```vue
<template>
  <div v-if="type === 'A'">Type A</div>
  <div v-else-if="type === 'B'">Type B</div>
  <div v-else>Other</div>
</template>
```

**v-show (CSS 토글)**
```vue
<template>
  <div v-show="isVisible">Visible content</div>
</template>
```

**v-for (리스트 렌더링)**
```vue
<template>
  <ul>
    <li v-for="item in items" :key="item.id">
      {{ item.name }}
    </li>
  </ul>

  <!-- 인덱스 포함 -->
  <div v-for="(item, index) in items" :key="item.id">
    {{ index }}: {{ item.name }}
  </div>

  <!-- 객체 순회 -->
  <div v-for="(value, key) in object" :key="key">
    {{ key }}: {{ value }}
  </div>
</template>
```

**v-model (양방향 바인딩)**
```vue
<script setup>
import { ref } from 'vue'

const text = ref('')
const checked = ref(false)
const selected = ref('')
</script>

<template>
  <input v-model="text" type="text">
  <input v-model="checked" type="checkbox">
  <select v-model="selected">
    <option>A</option>
    <option>B</option>
  </select>
</template>
```

### 5. 라이프사이클 훅

```javascript
import { 
  onBeforeMount,
  onMounted,
  onBeforeUpdate,
  onUpdated,
  onBeforeUnmount,
  onUnmounted
} from 'vue'

// 마운트 전
onBeforeMount(() => {
  console.log('Before mount')
})

// 마운트 후 (가장 많이 사용)
onMounted(() => {
  console.log('Mounted - DOM 접근 가능')
  // API 호출, 초기 데이터 로드 등
})

// 업데이트 전
onBeforeUpdate(() => {
  console.log('Before update')
})

// 업데이트 후
onUpdated(() => {
  console.log('Updated')
})

// 언마운트 전
onBeforeUnmount(() => {
  console.log('Before unmount')
  // 이벤트 리스너 제거, 타이머 정리 등
})

// 언마운트 후
onUnmounted(() => {
  console.log('Unmounted')
})
```

## 🎨 스타일링

### Scoped Styles
```vue
<style scoped>
/* 이 컴포넌트에만 적용 */
.button {
  background: blue;
}
</style>
```

### Global Styles
```vue
<style>
/* 전역 스타일 */
* {
  box-sizing: border-box;
}
</style>
```

### CSS Modules
```vue
<template>
  <div :class="$style.container">
    <p :class="$style.text">Text</p>
  </div>
</template>

<style module>
.container {
  padding: 20px;
}

.text {
  color: red;
}
</style>
```

### Dynamic Classes
```vue
<script setup>
import { ref } from 'vue'

const isActive = ref(true)
const hasError = ref(false)
</script>

<template>
  <!-- 객체 문법 -->
  <div :class="{ active: isActive, error: hasError }">

  <!-- 배열 문법 -->
  <div :class="[isActive ? 'active' : '', 'base-class']">

  <!-- 혼합 -->
  <div :class="[{ active: isActive }, 'base-class']">
</template>
```

### Inline Styles
```vue
<script setup>
import { ref } from 'vue'

const color = ref('red')
const fontSize = ref(14)
</script>

<template>
  <!-- 객체 문법 -->
  <div :style="{ color: color, fontSize: fontSize + 'px' }">

  <!-- 배열 문법 -->
  <div :style="[baseStyles, overrideStyles]">
</template>
```

## 🔄 Props & Emits

### Props (부모 → 자식)
```vue
<!-- ParentComponent.vue -->
<script setup>
import { ref } from 'vue'
import ChildComponent from './ChildComponent.vue'

const userName = ref('John')
const userAge = ref(30)
</script>

<template>
  <ChildComponent 
    :name="userName" 
    :age="userAge"
    :is-active="true"
  />
</template>
```

```vue
<!-- ChildComponent.vue -->
<script setup>
// Props 정의
const props = defineProps({
  name: {
    type: String,
    required: true
  },
  age: {
    type: Number,
    default: 0
  },
  isActive: Boolean
})

// Props 사용
console.log(props.name)
</script>

<template>
  <div>
    <p>Name: {{ name }}</p>
    <p>Age: {{ age }}</p>
    <p>Active: {{ isActive }}</p>
  </div>
</template>
```

### Emits (자식 → 부모)
```vue
<!-- ChildComponent.vue -->
<script setup>
const emit = defineEmits(['update', 'delete'])

function handleUpdate() {
  emit('update', { id: 1, name: 'Updated' })
}

function handleDelete(id) {
  emit('delete', id)
}
</script>

<template>
  <button @click="handleUpdate">Update</button>
  <button @click="handleDelete(1)">Delete</button>
</template>
```

```vue
<!-- ParentComponent.vue -->
<script setup>
import ChildComponent from './ChildComponent.vue'

function onUpdate(data) {
  console.log('Updated:', data)
}

function onDelete(id) {
  console.log('Deleted:', id)
}
</script>

<template>
  <ChildComponent 
    @update="onUpdate"
    @delete="onDelete"
  />
</template>
```

## 🎯 제공/주입 (Provide/Inject)

깊은 컴포넌트 트리에서 데이터 전달:

```vue
<!-- ParentComponent.vue -->
<script setup>
import { ref, provide } from 'vue'

const theme = ref('dark')
const updateTheme = (newTheme) => {
  theme.value = newTheme
}

// 제공
provide('theme', theme)
provide('updateTheme', updateTheme)
</script>
```

```vue
<!-- DeepChildComponent.vue -->
<script setup>
import { inject } from 'vue'

// 주입
const theme = inject('theme')
const updateTheme = inject('updateTheme')
</script>

<template>
  <div>
    <p>Current theme: {{ theme }}</p>
    <button @click="updateTheme('light')">Light</button>
  </div>
</template>
```

## 📚 다음 단계

이제 기초를 익혔다면:
1. [라우팅 가이드](./ROUTING.md) - Vue Router 사용법
2. [상태 관리 가이드](./STATE_MANAGEMENT.md) - Pinia 사용법
3. [API 통신 가이드](./API_COMMUNICATION.md) - Axios 사용법
4. [컴포넌트 개발 가이드](./COMPONENT_DEVELOPMENT.md) - 재사용 가능한 컴포넌트 작성

## 🔗 참고 자료

- [Vue.js 공식 문서](https://vuejs.org/)
- [Vue Router 공식 문서](https://router.vuejs.org/)
- [Pinia 공식 문서](https://pinia.vuejs.org/)
- [Vite 공식 문서](https://vitejs.dev/)
