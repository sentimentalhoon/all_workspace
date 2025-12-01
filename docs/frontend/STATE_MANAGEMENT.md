# Pinia 상태 관리 가이드

## 📋 개요

Pinia는 Vue 3을 위한 공식 상태 관리 라이브러리입니다. Vuex의 후속 버전으로, 더 간단하고 타입스크립트 친화적입니다.

**버전**: Pinia 2.3.0

## 🎯 주요 특징

- ✅ **간단한 API** - Vuex보다 훨씬 단순
- ✅ **타입 안전** - TypeScript 완벽 지원
- ✅ **모듈 자동 분할** - 코드 스플리팅 자동 지원
- ✅ **DevTools** - Vue DevTools 통합
- ✅ **Hot Module Replacement** - 개발 중 상태 유지
- ✅ **플러그인 시스템** - 확장 가능

## 🏗️ 기본 설정

### Pinia 설치 및 등록

```javascript
// src/main.js
import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.mount("#app");
```

## 📦 스토어 정의

### Setup Store (권장)

Composition API 스타일, 더 유연함:

```javascript
// src/stores/counter.js
import { ref, computed } from "vue";
import { defineStore } from "pinia";

export const useCounterStore = defineStore("counter", () => {
  // State (ref)
  const count = ref(0);
  const name = ref("Counter");

  // Getters (computed)
  const doubleCount = computed(() => count.value * 2);
  const isPositive = computed(() => count.value > 0);

  // Actions (functions)
  function increment() {
    count.value++;
  }

  function decrement() {
    count.value--;
  }

  function incrementBy(amount) {
    count.value += amount;
  }

  async function fetchData() {
    try {
      const response = await fetch("/api/data");
      const data = await response.json();
      count.value = data.count;
    } catch (error) {
      console.error("Failed to fetch:", error);
    }
  }

  // 반드시 state, getters, actions를 return
  return {
    // State
    count,
    name,
    // Getters
    doubleCount,
    isPositive,
    // Actions
    increment,
    decrement,
    incrementBy,
    fetchData,
  };
});
```

### Option Store

Vuex와 유사한 스타일:

```javascript
// src/stores/user.js
import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
  // State
  state: () => ({
    user: null,
    isLoggedIn: false,
    preferences: {
      theme: "light",
      language: "ko",
    },
  }),

  // Getters
  getters: {
    userName: (state) => state.user?.name || "Guest",

    userEmail(state) {
      return state.user?.email;
    },

    // 다른 getter 사용
    userInfo(state) {
      return `${this.userName} (${state.user?.email})`;
    },

    // 인자를 받는 getter
    hasPermission: (state) => {
      return (permission) => {
        return state.user?.permissions?.includes(permission);
      };
    },
  },

  // Actions
  actions: {
    setUser(user) {
      this.user = user;
      this.isLoggedIn = true;
    },

    logout() {
      this.user = null;
      this.isLoggedIn = false;
    },

    updatePreferences(prefs) {
      this.preferences = { ...this.preferences, ...prefs };
    },

    async login(credentials) {
      try {
        const response = await fetch("/api/login", {
          method: "POST",
          body: JSON.stringify(credentials),
        });
        const data = await response.json();
        this.setUser(data.user);
        return data;
      } catch (error) {
        console.error("Login failed:", error);
        throw error;
      }
    },
  },
});
```

## 💡 스토어 사용하기

### 컴포넌트에서 사용

```vue
<script setup>
import { useCounterStore } from "@/stores/counter";
import { useUserStore } from "@/stores/user";

const counterStore = useCounterStore();
const userStore = useUserStore();

// State 직접 접근
console.log(counterStore.count);
console.log(userStore.userName);

// Getters 접근
console.log(counterStore.doubleCount);

// Actions 호출
function handleIncrement() {
  counterStore.increment();
}

async function handleLogin() {
  try {
    await userStore.login({ email: "user@example.com", password: "pass" });
  } catch (error) {
    console.error(error);
  }
}
</script>

<template>
  <div>
    <h2>Count: {{ counterStore.count }}</h2>
    <p>Double: {{ counterStore.doubleCount }}</p>
    <button @click="handleIncrement">+1</button>

    <div v-if="userStore.isLoggedIn">
      <p>Welcome, {{ userStore.userName }}!</p>
      <button @click="userStore.logout">Logout</button>
    </div>
  </div>
</template>
```

### 구조 분해 할당

**주의**: 반응성을 유지하려면 `storeToRefs` 사용!

```vue
<script setup>
import { storeToRefs } from "pinia";
import { useCounterStore } from "@/stores/counter";

const counterStore = useCounterStore();

// ❌ 반응성 손실
const { count, doubleCount } = counterStore;

// ✅ 반응성 유지 (state와 getters만)
const { count, doubleCount } = storeToRefs(counterStore);

// ✅ Actions는 직접 구조 분해 가능
const { increment, decrement } = counterStore;
</script>

<template>
  <div>
    <p>{{ count }}</p>
    <button @click="increment">+</button>
  </div>
</template>
```

## 🔄 State 업데이트

### 직접 변경

```javascript
const store = useCounterStore();

// 직접 변경 가능
store.count++;
store.name = "New Name";
```

### $patch 사용 (권장)

여러 값을 한 번에 업데이트:

```javascript
// 객체 방식
store.$patch({
  count: store.count + 1,
  name: "Updated Name",
});

// 함수 방식 (더 복잡한 로직)
store.$patch((state) => {
  state.count++;
  state.items.push({ name: "New Item" });
  state.hasChanged = true;
});
```

### $state 교체

```javascript
// 전체 state 교체
store.$state = {
  count: 0,
  name: "Reset",
};
```

### $reset (Option Store만 가능)

```javascript
// 초기 상태로 리셋
store.$reset();
```

Setup Store에서 리셋:

```javascript
// stores/counter.js
export const useCounterStore = defineStore("counter", () => {
  const count = ref(0);
  const name = ref("Counter");

  function $reset() {
    count.value = 0;
    name.value = "Counter";
  }

  return { count, name, $reset };
});
```

## 🎯 Getters 고급

### 다른 Store의 Getter 사용

```javascript
// stores/cart.js
export const useCartStore = defineStore("cart", () => {
  const items = ref([]);

  const total = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price, 0);
  });

  return { items, total };
});

// stores/checkout.js
export const useCheckoutStore = defineStore("checkout", () => {
  const cartStore = useCartStore();

  const totalWithTax = computed(() => {
    return cartStore.total * 1.1;
  });

  return { totalWithTax };
});
```

### 인자를 받는 Getter

```javascript
export const useProductStore = defineStore("products", () => {
  const products = ref([
    { id: 1, name: "Product 1", category: "A" },
    { id: 2, name: "Product 2", category: "B" },
  ]);

  // 함수를 반환하는 computed
  const productsByCategory = computed(() => {
    return (category) => {
      return products.value.filter((p) => p.category === category);
    };
  });

  return { products, productsByCategory };
});
```

사용:

```vue
<script setup>
const store = useProductStore();
const categoryAProducts = store.productsByCategory("A");
</script>
```

## ⚡ Actions 고급

### 비동기 Actions

```javascript
export const useDataStore = defineStore("data", () => {
  const data = ref(null);
  const loading = ref(false);
  const error = ref(null);

  async function fetchData() {
    loading.value = true;
    error.value = null;

    try {
      const response = await fetch("/api/data");
      if (!response.ok) throw new Error("Failed to fetch");

      data.value = await response.json();
    } catch (err) {
      error.value = err.message;
    } finally {
      loading.value = false;
    }
  }

  return { data, loading, error, fetchData };
});
```

### 다른 Store의 Actions 호출

```javascript
export const useAuthStore = defineStore("auth", () => {
  const user = ref(null);
  const cartStore = useCartStore();

  async function logout() {
    user.value = null;
    cartStore.clear(); // 다른 스토어의 액션 호출
    await fetch("/api/logout", { method: "POST" });
  }

  return { user, logout };
});
```

### Actions에서 $subscribe 사용

```javascript
export const useLogStore = defineStore("log", () => {
  const logs = ref([]);

  function addLog(message) {
    logs.value.push({
      message,
      timestamp: new Date(),
    });
  }

  return { logs, addLog };
});

// 다른 스토어에서
export const useCounterStore = defineStore("counter", () => {
  const count = ref(0);
  const logStore = useLogStore();

  function increment() {
    count.value++;
    logStore.addLog(`Counter incremented to ${count.value}`);
  }

  return { count, increment };
});
```

## 🔌 플러그인

### 로컬 스토리지 플러그인

```javascript
// src/plugins/piniaLocalStorage.js
export function piniaLocalStorage() {
  return (context) => {
    const { store } = context;

    // 초기화 시 로컬 스토리지에서 복원
    const savedState = localStorage.getItem(store.$id);
    if (savedState) {
      store.$patch(JSON.parse(savedState));
    }

    // 상태 변경 시 로컬 스토리지에 저장
    store.$subscribe((mutation, state) => {
      localStorage.setItem(store.$id, JSON.stringify(state));
    });
  };
}
```

```javascript
// src/main.js
import { createPinia } from "pinia";
import { piniaLocalStorage } from "./plugins/piniaLocalStorage";

const pinia = createPinia();
pinia.use(piniaLocalStorage());

app.use(pinia);
```

### 로깅 플러그인

```javascript
// src/plugins/piniaLogger.js
export function piniaLogger() {
  return (context) => {
    const { store } = context;

    store.$onAction(({ name, args, after, onError }) => {
      console.log(`[${store.$id}] Action ${name} called with:`, args);

      after((result) => {
        console.log(`[${store.$id}] Action ${name} finished with:`, result);
      });

      onError((error) => {
        console.error(`[${store.$id}] Action ${name} failed:`, error);
      });
    });
  };
}
```

## 📊 State 구독

### $subscribe - State 변경 감지

```javascript
const counterStore = useCounterStore();

counterStore.$subscribe((mutation, state) => {
  console.log("Type:", mutation.type); // 'direct' | 'patch object' | 'patch function'
  console.log("Store ID:", mutation.storeId);
  console.log("Payload:", mutation.payload);
  console.log("Current State:", state);
});

// 컴포넌트 언마운트 후에도 유지
counterStore.$subscribe(callback, { detached: true });
```

### $onAction - Action 실행 감지

```javascript
const unsubscribe = counterStore.$onAction(
  ({
    name, // 액션 이름
    store, // 스토어 인스턴스
    args, // 액션 인자
    after, // 액션 성공 후 훅
    onError, // 액션 실패 시 훅
  }) => {
    console.log(`Action ${name} started`);

    after((result) => {
      console.log(`Action ${name} finished:`, result);
    });

    onError((error) => {
      console.error(`Action ${name} failed:`, error);
    });
  }
);

// 구독 해제
unsubscribe();
```

## 🧪 테스팅

### 스토어 테스트

```javascript
import { setActivePinia, createPinia } from "pinia";
import { useCounterStore } from "@/stores/counter";

describe("Counter Store", () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it("increments counter", () => {
    const store = useCounterStore();
    expect(store.count).toBe(0);

    store.increment();
    expect(store.count).toBe(1);
  });

  it("calculates double count", () => {
    const store = useCounterStore();
    store.count = 5;
    expect(store.doubleCount).toBe(10);
  });
});
```

### 컴포넌트 테스트

```javascript
import { mount } from "@vue/test-utils";
import { createPinia, setActivePinia } from "pinia";
import Counter from "@/components/Counter.vue";

describe("Counter Component", () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it("displays count from store", () => {
    const wrapper = mount(Counter, {
      global: {
        plugins: [createPinia()],
      },
    });

    expect(wrapper.text()).toContain("0");
  });
});
```

## 📚 실전 예제

### 인증 스토어

```javascript
// stores/auth.js
import { ref, computed } from "vue";
import { defineStore } from "pinia";
import axios from "axios";

export const useAuthStore = defineStore("auth", () => {
  const user = ref(null);
  const token = ref(localStorage.getItem("token"));
  const loading = ref(false);

  const isAuthenticated = computed(() => !!token.value);
  const userName = computed(() => user.value?.name || "Guest");

  async function login(credentials) {
    loading.value = true;
    try {
      const { data } = await axios.post("/api/login", credentials);
      token.value = data.token;
      user.value = data.user;
      localStorage.setItem("token", data.token);
      axios.defaults.headers.common["Authorization"] = `Bearer ${data.token}`;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  }

  async function logout() {
    try {
      await axios.post("/api/logout");
    } finally {
      user.value = null;
      token.value = null;
      localStorage.removeItem("token");
      delete axios.defaults.headers.common["Authorization"];
    }
  }

  async function fetchUser() {
    if (!token.value) return;

    try {
      const { data } = await axios.get("/api/user");
      user.value = data;
    } catch (error) {
      logout();
    }
  }

  // 초기화
  if (token.value) {
    axios.defaults.headers.common["Authorization"] = `Bearer ${token.value}`;
    fetchUser();
  }

  return {
    user,
    token,
    loading,
    isAuthenticated,
    userName,
    login,
    logout,
    fetchUser,
  };
});
```

### 장바구니 스토어

```javascript
// stores/cart.js
import { ref, computed } from "vue";
import { defineStore } from "pinia";

export const useCartStore = defineStore("cart", () => {
  const items = ref([]);

  const totalItems = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0);
  });

  const totalPrice = computed(() => {
    return items.value.reduce(
      (sum, item) => sum + item.price * item.quantity,
      0
    );
  });

  function addItem(product) {
    const existingItem = items.value.find((item) => item.id === product.id);

    if (existingItem) {
      existingItem.quantity++;
    } else {
      items.value.push({
        ...product,
        quantity: 1,
      });
    }
  }

  function removeItem(productId) {
    const index = items.value.findIndex((item) => item.id === productId);
    if (index > -1) {
      items.value.splice(index, 1);
    }
  }

  function updateQuantity(productId, quantity) {
    const item = items.value.find((item) => item.id === productId);
    if (item) {
      item.quantity = Math.max(0, quantity);
      if (item.quantity === 0) {
        removeItem(productId);
      }
    }
  }

  function clear() {
    items.value = [];
  }

  return {
    items,
    totalItems,
    totalPrice,
    addItem,
    removeItem,
    updateQuantity,
    clear,
  };
});
```

## 🔗 참고 자료

- [Pinia 공식 문서](https://pinia.vuejs.org/)
- [Pinia vs Vuex](https://pinia.vuejs.org/introduction.html#comparison-with-vuex)
