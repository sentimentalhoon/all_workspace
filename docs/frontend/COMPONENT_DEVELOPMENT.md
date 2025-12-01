# 컴포넌트 개발 가이드

## 📋 개요

Vue.js 3 Composition API를 사용한 컴포넌트 개발 모범 사례와 패턴을 다룹니다.

## 🏗️ 컴포넌트 구조

### 기본 컴포넌트 템플릿

```vue
<script setup>
import { ref, computed, onMounted } from 'vue'

// Props 정의
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  count: {
    type: Number,
    default: 0
  }
})

// Emits 정의
const emit = defineEmits(['update', 'delete'])

// State
const localCount = ref(props.count)

// Computed
const doubleCount = computed(() => localCount.value * 2)

// Methods
function increment() {
  localCount.value++
  emit('update', localCount.value)
}

// Lifecycle
onMounted(() => {
  console.log('Component mounted')
})
</script>

<template>
  <div class="my-component">
    <h2>{{ title }}</h2>
    <p>Count: {{ localCount }}</p>
    <p>Double: {{ doubleCount }}</p>
    <button @click="increment">Increment</button>
  </div>
</template>

<style scoped>
.my-component {
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>
```

## 📦 Props 패턴

### Props 검증

```vue
<script setup>
const props = defineProps({
  // 기본 타입
  title: String,
  count: Number,
  isActive: Boolean,
  tags: Array,
  user: Object,
  callback: Function,

  // 필수 prop
  id: {
    type: [String, Number],
    required: true
  },

  // 기본값
  status: {
    type: String,
    default: 'pending'
  },

  // 객체/배열 기본값 (함수로 반환)
  items: {
    type: Array,
    default: () => []
  },

  config: {
    type: Object,
    default: () => ({
      theme: 'light'
    })
  },

  // 커스텀 검증
  age: {
    type: Number,
    validator: (value) => {
      return value >= 0 && value <= 150
    }
  },

  // 여러 타입 허용
  value: {
    type: [String, Number, Boolean],
    default: ''
  }
})
</script>
```

### Props 활용 패턴

```vue
<script setup>
const props = defineProps({
  modelValue: String,
  disabled: Boolean
})

const emit = defineEmits(['update:modelValue'])

// Props 직접 사용
console.log(props.modelValue)

// Computed로 래핑 (읽기 전용)
const value = computed(() => props.modelValue)

// v-model 양방향 바인딩
const localValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})
</script>

<template>
  <input v-model="localValue" :disabled="disabled" />
</template>
```

### Props Destructuring (주의!)

```vue
<script setup>
const props = defineProps({
  count: Number
})

// ❌ 반응성 손실
const { count } = props

// ✅ toRefs 사용
import { toRefs } from 'vue'
const { count } = toRefs(props)

// ✅ 또는 직접 접근
console.log(props.count)
</script>
```

## 📤 Events (Emits)

### 기본 이벤트

```vue
<script setup>
const emit = defineEmits(['submit', 'cancel', 'update'])

function handleSubmit() {
  emit('submit')
}

function handleUpdate(value) {
  emit('update', value)
}

function handleMultipleArgs() {
  emit('update', 'value1', 'value2', { key: 'value3' })
}
</script>

<template>
  <button @click="handleSubmit">Submit</button>
  <button @click="emit('cancel')">Cancel</button>
</template>
```

### 이벤트 검증

```vue
<script setup>
const emit = defineEmits({
  // 검증 없음
  click: null,

  // 검증 함수
  submit: (payload) => {
    if (payload.email && payload.password) {
      return true
    } else {
      console.warn('Invalid submit payload')
      return false
    }
  },

  update: (id, value) => {
    return typeof id === 'number' && value !== undefined
  }
})

function handleSubmit() {
  emit('submit', {
    email: 'user@example.com',
    password: 'secret'
  })
}
</script>
```

### v-model 구현

```vue
<!-- CustomInput.vue -->
<script setup>
const props = defineProps({
  modelValue: String
})

const emit = defineEmits(['update:modelValue'])

function updateValue(event) {
  emit('update:modelValue', event.target.value)
}
</script>

<template>
  <input 
    :value="modelValue" 
    @input="updateValue"
  />
</template>
```

사용:

```vue
<script setup>
import CustomInput from './CustomInput.vue'

const text = ref('')
</script>

<template>
  <CustomInput v-model="text" />
</template>
```

### 다중 v-model

```vue
<!-- UserForm.vue -->
<script setup>
defineProps({
  firstName: String,
  lastName: String
})

const emit = defineEmits([
  'update:firstName',
  'update:lastName'
])
</script>

<template>
  <input 
    :value="firstName"
    @input="emit('update:firstName', $event.target.value)"
  />
  <input 
    :value="lastName"
    @input="emit('update:lastName', $event.target.value)"
  />
</template>
```

사용:

```vue
<script setup>
const firstName = ref('John')
const lastName = ref('Doe')
</script>

<template>
  <UserForm 
    v-model:first-name="firstName"
    v-model:last-name="lastName"
  />
</template>
```

## 🎰 Slots

### 기본 슬롯

```vue
<!-- Card.vue -->
<template>
  <div class="card">
    <slot>Default content</slot>
  </div>
</template>
```

사용:

```vue
<Card>
  <p>This is custom content</p>
</Card>
```

### Named Slots

```vue
<!-- Layout.vue -->
<template>
  <div class="layout">
    <header>
      <slot name="header">Default Header</slot>
    </header>
    
    <main>
      <slot>Default Content</slot>
    </main>
    
    <footer>
      <slot name="footer">Default Footer</slot>
    </footer>
  </div>
</template>
```

사용:

```vue
<Layout>
  <template #header>
    <h1>My Header</h1>
  </template>

  <p>Main content goes here</p>

  <template #footer>
    <p>© 2025</p>
  </template>
</Layout>
```

### Scoped Slots

```vue
<!-- UserList.vue -->
<script setup>
const users = ref([
  { id: 1, name: 'John', age: 30 },
  { id: 2, name: 'Jane', age: 25 }
])
</script>

<template>
  <ul>
    <li v-for="user in users" :key="user.id">
      <slot :user="user" :index="index">
        {{ user.name }}
      </slot>
    </li>
  </ul>
</template>
```

사용:

```vue
<UserList>
  <template #default="{ user, index }">
    <strong>{{ index + 1 }}.</strong>
    {{ user.name }} ({{ user.age }})
  </template>
</UserList>
```

### 동적 슬롯

```vue
<script setup>
const slotName = ref('header')
</script>

<template>
  <Layout>
    <template #[slotName]>
      Dynamic slot content
    </template>
  </Layout>
</template>
```

## 🔧 Composables

### 재사용 가능한 로직

```javascript
// composables/useCounter.js
import { ref, computed } from 'vue'

export function useCounter(initialValue = 0) {
  const count = ref(initialValue)
  const double = computed(() => count.value * 2)

  function increment() {
    count.value++
  }

  function decrement() {
    count.value--
  }

  function reset() {
    count.value = initialValue
  }

  return {
    count,
    double,
    increment,
    decrement,
    reset
  }
}
```

사용:

```vue
<script setup>
import { useCounter } from '@/composables/useCounter'

const { count, double, increment, decrement, reset } = useCounter(10)
</script>

<template>
  <div>
    <p>Count: {{ count }}</p>
    <p>Double: {{ double }}</p>
    <button @click="increment">+</button>
    <button @click="decrement">-</button>
    <button @click="reset">Reset</button>
  </div>
</template>
```

### 마우스 위치 트래킹

```javascript
// composables/useMouse.js
import { ref, onMounted, onUnmounted } from 'vue'

export function useMouse() {
  const x = ref(0)
  const y = ref(0)

  function update(event) {
    x.value = event.pageX
    y.value = event.pageY
  }

  onMounted(() => {
    window.addEventListener('mousemove', update)
  })

  onUnmounted(() => {
    window.removeEventListener('mousemove', update)
  })

  return { x, y }
}
```

### 로컬 스토리지 Composable

```javascript
// composables/useLocalStorage.js
import { ref, watch } from 'vue'

export function useLocalStorage(key, defaultValue) {
  const storedValue = localStorage.getItem(key)
  const value = ref(storedValue ? JSON.parse(storedValue) : defaultValue)

  watch(value, (newValue) => {
    localStorage.setItem(key, JSON.stringify(newValue))
  }, { deep: true })

  return value
}
```

사용:

```vue
<script setup>
import { useLocalStorage } from '@/composables/useLocalStorage'

const theme = useLocalStorage('theme', 'light')
const settings = useLocalStorage('settings', { notifications: true })
</script>

<template>
  <div>
    <select v-model="theme">
      <option>light</option>
      <option>dark</option>
    </select>
  </div>
</template>
```

## 🎨 동적 컴포넌트

### component :is

```vue
<script setup>
import { ref, shallowRef } from 'vue'
import ComponentA from './ComponentA.vue'
import ComponentB from './ComponentB.vue'

// shallowRef 사용 (컴포넌트는 깊은 반응성 불필요)
const currentComponent = shallowRef(ComponentA)

function switchComponent() {
  currentComponent.value = 
    currentComponent.value === ComponentA ? ComponentB : ComponentA
}
</script>

<template>
  <button @click="switchComponent">Switch</button>
  <component :is="currentComponent" />
</template>
```

### KeepAlive

```vue
<script setup>
import { ref } from 'vue'
import TabA from './TabA.vue'
import TabB from './TabB.vue'

const tabs = {
  TabA,
  TabB
}

const currentTab = ref('TabA')
</script>

<template>
  <button 
    v-for="(_, tab) in tabs" 
    :key="tab"
    @click="currentTab = tab"
  >
    {{ tab }}
  </button>

  <KeepAlive>
    <component :is="tabs[currentTab]" />
  </KeepAlive>
</template>
```

### KeepAlive with include/exclude

```vue
<template>
  <!-- TabA, TabB만 캐시 -->
  <KeepAlive :include="['TabA', 'TabB']">
    <component :is="currentTab" />
  </KeepAlive>

  <!-- TabC 제외하고 캐시 -->
  <KeepAlive :exclude="['TabC']">
    <component :is="currentTab" />
  </KeepAlive>

  <!-- 최대 3개까지만 캐시 -->
  <KeepAlive :max="3">
    <component :is="currentTab" />
  </KeepAlive>
</template>
```

## 🔄 비동기 컴포넌트

### defineAsyncComponent

```javascript
import { defineAsyncComponent } from 'vue'

// 기본 사용
const AsyncComponent = defineAsyncComponent(() =>
  import('./components/HeavyComponent.vue')
)

// 로딩/에러 상태
const AsyncComponentWithOptions = defineAsyncComponent({
  loader: () => import('./components/HeavyComponent.vue'),
  loadingComponent: LoadingSpinner,
  errorComponent: ErrorDisplay,
  delay: 200,  // 200ms 후 로딩 컴포넌트 표시
  timeout: 3000  // 3초 후 타임아웃
})
```

사용:

```vue
<script setup>
import { defineAsyncComponent } from 'vue'

const HeavyComponent = defineAsyncComponent(() =>
  import('./HeavyComponent.vue')
)
</script>

<template>
  <Suspense>
    <HeavyComponent />
    <template #fallback>
      <div>Loading...</div>
    </template>
  </Suspense>
</template>
```

## 🎯 Provide / Inject

### 부모에서 제공

```vue
<!-- App.vue -->
<script setup>
import { ref, provide } from 'vue'

const theme = ref('light')
const toggleTheme = () => {
  theme.value = theme.value === 'light' ? 'dark' : 'light'
}

// 제공
provide('theme', theme)
provide('toggleTheme', toggleTheme)
</script>

<template>
  <ChildComponent />
</template>
```

### 자식에서 주입

```vue
<!-- ChildComponent.vue -->
<script setup>
import { inject } from 'vue'

const theme = inject('theme')
const toggleTheme = inject('toggleTheme')

// 기본값 제공
const userSettings = inject('userSettings', { notifications: true })

// 기본값을 함수로 (비용이 큰 경우)
const config = inject('config', () => ({ /* default config */ }), true)
</script>

<template>
  <div :class="theme">
    <button @click="toggleTheme">Toggle Theme</button>
  </div>
</template>
```

### 타입 안전한 Provide/Inject

```javascript
// keys.js
import { InjectionKey } from 'vue'

export const themeKey = Symbol()
export const userKey = Symbol()
```

```vue
<!-- Provider -->
<script setup>
import { provide, ref } from 'vue'
import { themeKey } from './keys'

const theme = ref('light')
provide(themeKey, theme)
</script>
```

```vue
<!-- Consumer -->
<script setup>
import { inject } from 'vue'
import { themeKey } from './keys'

const theme = inject(themeKey)
</script>
```

## 📋 Template Refs

### 기본 ref

```vue
<script setup>
import { ref, onMounted } from 'vue'

const input = ref(null)
const list = ref(null)

onMounted(() => {
  // DOM 엘리먼트 접근
  input.value.focus()
  console.log(list.value.children.length)
})
</script>

<template>
  <input ref="input" />
  <ul ref="list">
    <li>Item 1</li>
    <li>Item 2</li>
  </ul>
</template>
```

### 컴포넌트 ref

```vue
<script setup>
import { ref, onMounted } from 'vue'
import ChildComponent from './ChildComponent.vue'

const child = ref(null)

onMounted(() => {
  // 자식 컴포넌트 메서드 호출
  child.value.someMethod()
})
</script>

<template>
  <ChildComponent ref="child" />
</template>
```

자식 컴포넌트에서 노출:

```vue
<!-- ChildComponent.vue -->
<script setup>
import { ref } from 'vue'

const count = ref(0)

function someMethod() {
  console.log('Called from parent')
}

// 부모에게 노출할 것만 명시
defineExpose({
  count,
  someMethod
})
</script>
```

### v-for 내 ref

```vue
<script setup>
import { ref, onMounted } from 'vue'

const items = ref([1, 2, 3])
const itemRefs = ref([])

onMounted(() => {
  console.log(itemRefs.value)  // DOM 엘리먼트 배열
})
</script>

<template>
  <ul>
    <li 
      v-for="item in items" 
      :key="item"
      :ref="el => { if (el) itemRefs[item - 1] = el }"
    >
      {{ item }}
    </li>
  </ul>
</template>
```

## 🧪 테스팅

### 컴포넌트 테스트

```javascript
import { mount } from '@vue/test-utils'
import MyComponent from '@/components/MyComponent.vue'

describe('MyComponent', () => {
  it('renders properly', () => {
    const wrapper = mount(MyComponent, {
      props: {
        title: 'Test Title'
      }
    })

    expect(wrapper.text()).toContain('Test Title')
  })

  it('emits event on button click', async () => {
    const wrapper = mount(MyComponent)
    
    await wrapper.find('button').trigger('click')
    
    expect(wrapper.emitted()).toHaveProperty('submit')
    expect(wrapper.emitted('submit')).toHaveLength(1)
  })

  it('updates data when prop changes', async () => {
    const wrapper = mount(MyComponent, {
      props: { count: 0 }
    })

    await wrapper.setProps({ count: 5 })
    
    expect(wrapper.vm.count).toBe(5)
  })
})
```

## 📚 모범 사례

### 1. Props는 One-way Data Flow

```vue
<script setup>
const props = defineProps({
  modelValue: String
})

// ❌ Props 직접 수정 금지
// props.modelValue = 'new value'

// ✅ 로컬 state로 복사
const localValue = ref(props.modelValue)

// ✅ Computed + emit 사용
const emit = defineEmits(['update:modelValue'])
const value = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})
</script>
```

### 2. 이벤트 명명 규칙

```vue
<script setup>
// ✅ kebab-case 사용
const emit = defineEmits([
  'update-value',
  'delete-item',
  'submit-form'
])

// ❌ camelCase 피하기 (HTML은 대소문자 구분 안함)
// const emit = defineEmits(['updateValue'])
</script>

<template>
  <button @click="emit('submit-form')">Submit</button>
</template>
```

### 3. 컴포넌트 크기 최소화

```vue
<!-- ❌ 너무 큰 컴포넌트 -->
<script setup>
// 500 lines of code...
</script>

<!-- ✅ 작은 컴포넌트로 분리 -->
<script setup>
import UserProfile from './UserProfile.vue'
import UserPosts from './UserPosts.vue'
import UserStats from './UserStats.vue'
</script>

<template>
  <div>
    <UserProfile :user="user" />
    <UserPosts :posts="posts" />
    <UserStats :stats="stats" />
  </div>
</template>
```

### 4. Composables 활용

```javascript
// ❌ 로직을 컴포넌트에 직접 작성
<script setup>
const loading = ref(false)
const error = ref(null)
const data = ref(null)

async function fetchData() {
  // ...
}
</script>

// ✅ Composable로 추출
<script setup>
import { useFetch } from '@/composables/useFetch'

const { data, loading, error } = useFetch('/api/users')
</script>
```

## 🔗 참고 자료

- [Vue 3 컴포넌트 기초](https://vuejs.org/guide/essentials/component-basics.html)
- [Composition API FAQ](https://vuejs.org/guide/extras/composition-api-faq.html)
