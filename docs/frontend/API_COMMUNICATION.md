# API 통신 가이드 (Axios)

## 📋 개요

Axios는 Promise 기반 HTTP 클라이언트로, 브라우저와 Node.js에서 HTTP 요청을 처리합니다.

**버전**: Axios 1.7.9

## 🏗️ 기본 설정

### Axios 인스턴스 생성

```javascript
// src/api/axios.js
import axios from 'axios'

// 기본 인스턴스
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export default api
```

### 환경 변수 설정

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8080/api

# .env.production
VITE_API_BASE_URL=https://mycamp.duckdns.org/api
```

## 🔐 인터셉터

### 요청 인터셉터

```javascript
// src/api/axios.js
import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    // 요청 전 처리
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 로딩 상태 표시
    console.log(`Request: ${config.method?.toUpperCase()} ${config.url}`)

    return config
  },
  (error) => {
    // 요청 에러 처리
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

export default api
```

### 응답 인터셉터

```javascript
// src/api/axios.js
api.interceptors.response.use(
  (response) => {
    // 응답 데이터 처리
    console.log(`Response: ${response.status} ${response.config.url}`)
    return response.data  // data만 추출하여 반환
  },
  (error) => {
    // 에러 응답 처리
    if (error.response) {
      // 서버가 응답한 에러
      const { status, data } = error.response

      switch (status) {
        case 401:
          // 인증 실패 - 로그인 페이지로 리다이렉트
          console.error('Unauthorized')
          localStorage.removeItem('token')
          window.location.href = '/login'
          break

        case 403:
          // 권한 없음
          console.error('Forbidden')
          break

        case 404:
          // 리소스 없음
          console.error('Not Found')
          break

        case 500:
          // 서버 에러
          console.error('Server Error')
          break

        default:
          console.error('Error:', data.message || 'Unknown error')
      }

      return Promise.reject(data)
    } else if (error.request) {
      // 요청은 보냈지만 응답이 없음
      console.error('No response from server')
      return Promise.reject({ message: 'Network Error' })
    } else {
      // 요청 설정 중 에러
      console.error('Request setup error:', error.message)
      return Promise.reject(error)
    }
  }
)
```

## 📡 API 함수 작성

### REST API 패턴

```javascript
// src/api/user.js
import api from './axios'

export const userAPI = {
  // GET 요청
  getUser(id) {
    return api.get(`/users/${id}`)
  },

  // GET 요청 (쿼리 파라미터)
  getUsers(params) {
    return api.get('/users', { params })
    // /users?page=1&limit=10
  },

  // POST 요청
  createUser(userData) {
    return api.post('/users', userData)
  },

  // PUT 요청 (전체 업데이트)
  updateUser(id, userData) {
    return api.put(`/users/${id}`, userData)
  },

  // PATCH 요청 (부분 업데이트)
  patchUser(id, partialData) {
    return api.patch(`/users/${id}`, partialData)
  },

  // DELETE 요청
  deleteUser(id) {
    return api.delete(`/users/${id}`)
  }
}
```

```javascript
// src/api/auth.js
import api from './axios'

export const authAPI = {
  login(credentials) {
    return api.post('/auth/login', credentials)
  },

  logout() {
    return api.post('/auth/logout')
  },

  register(userData) {
    return api.post('/auth/register', userData)
  },

  refreshToken() {
    return api.post('/auth/refresh')
  },

  forgotPassword(email) {
    return api.post('/auth/forgot-password', { email })
  },

  resetPassword(token, password) {
    return api.post('/auth/reset-password', { token, password })
  }
}
```

## 💡 컴포넌트에서 사용

### Composition API 패턴

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { userAPI } from '@/api/user'

const users = ref([])
const loading = ref(false)
const error = ref(null)

async function fetchUsers() {
  loading.value = true
  error.value = null

  try {
    const data = await userAPI.getUsers({ page: 1, limit: 10 })
    users.value = data.users
  } catch (err) {
    error.value = err.message || 'Failed to fetch users'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div>
    <div v-if="loading">Loading...</div>
    <div v-else-if="error">{{ error }}</div>
    <ul v-else>
      <li v-for="user in users" :key="user.id">
        {{ user.name }}
      </li>
    </ul>
  </div>
</template>
```

### Composable 패턴 (재사용)

```javascript
// src/composables/useApi.js
import { ref } from 'vue'

export function useApi(apiFunction) {
  const data = ref(null)
  const loading = ref(false)
  const error = ref(null)

  async function execute(...args) {
    loading.value = true
    error.value = null

    try {
      data.value = await apiFunction(...args)
      return data.value
    } catch (err) {
      error.value = err.message || 'An error occurred'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    data,
    loading,
    error,
    execute
  }
}
```

사용 예시:

```vue
<script setup>
import { onMounted } from 'vue'
import { useApi } from '@/composables/useApi'
import { userAPI } from '@/api/user'

const { data: users, loading, error, execute: fetchUsers } = useApi(userAPI.getUsers)

onMounted(() => {
  fetchUsers({ page: 1, limit: 10 })
})
</script>

<template>
  <div>
    <div v-if="loading">Loading...</div>
    <div v-else-if="error">{{ error }}</div>
    <ul v-else-if="users">
      <li v-for="user in users.users" :key="user.id">
        {{ user.name }}
      </li>
    </ul>
  </div>
</template>
```

## 🔄 CRUD 패턴

### 리스트 + 생성/수정/삭제

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { userAPI } from '@/api/user'

const users = ref([])
const loading = ref(false)
const error = ref(null)

// 목록 조회
async function fetchUsers() {
  loading.value = true
  try {
    const data = await userAPI.getUsers()
    users.value = data.users
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

// 생성
async function createUser(userData) {
  try {
    const newUser = await userAPI.createUser(userData)
    users.value.push(newUser)
  } catch (err) {
    console.error('Failed to create user:', err)
  }
}

// 수정
async function updateUser(id, userData) {
  try {
    const updatedUser = await userAPI.updateUser(id, userData)
    const index = users.value.findIndex(u => u.id === id)
    if (index !== -1) {
      users.value[index] = updatedUser
    }
  } catch (err) {
    console.error('Failed to update user:', err)
  }
}

// 삭제
async function deleteUser(id) {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    await userAPI.deleteUser(id)
    users.value = users.value.filter(u => u.id !== id)
  } catch (err) {
    console.error('Failed to delete user:', err)
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div>
    <button @click="createUser({ name: 'New User' })">Add User</button>
    
    <div v-if="loading">Loading...</div>
    <div v-else-if="error">{{ error }}</div>
    <ul v-else>
      <li v-for="user in users" :key="user.id">
        {{ user.name }}
        <button @click="updateUser(user.id, { name: 'Updated' })">Edit</button>
        <button @click="deleteUser(user.id)">Delete</button>
      </li>
    </ul>
  </div>
</template>
```

## 📤 파일 업로드

### FormData 사용

```javascript
// src/api/upload.js
import api from './axios'

export const uploadAPI = {
  uploadFile(file, onUploadProgress) {
    const formData = new FormData()
    formData.append('file', file)

    return api.post('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        const percentCompleted = Math.round(
          (progressEvent.loaded * 100) / progressEvent.total
        )
        onUploadProgress?.(percentCompleted)
      }
    })
  },

  uploadMultipleFiles(files) {
    const formData = new FormData()
    files.forEach((file) => {
      formData.append('files', file)
    })

    return api.post('/upload/multiple', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
```

컴포넌트에서 사용:

```vue
<script setup>
import { ref } from 'vue'
import { uploadAPI } from '@/api/upload'

const selectedFile = ref(null)
const uploadProgress = ref(0)
const uploading = ref(false)

function handleFileChange(event) {
  selectedFile.value = event.target.files[0]
}

async function uploadFile() {
  if (!selectedFile.value) return

  uploading.value = true
  uploadProgress.value = 0

  try {
    const response = await uploadAPI.uploadFile(
      selectedFile.value,
      (progress) => {
        uploadProgress.value = progress
      }
    )
    console.log('Upload success:', response)
  } catch (error) {
    console.error('Upload failed:', error)
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <div>
    <input type="file" @change="handleFileChange" />
    <button @click="uploadFile" :disabled="!selectedFile || uploading">
      Upload
    </button>
    <div v-if="uploading">
      Progress: {{ uploadProgress }}%
    </div>
  </div>
</template>
```

## 🔄 요청 취소

### AbortController 사용

```vue
<script setup>
import { ref, onBeforeUnmount } from 'vue'
import api from '@/api/axios'

const data = ref(null)
const loading = ref(false)
let abortController = null

async function fetchData() {
  // 이전 요청 취소
  if (abortController) {
    abortController.abort()
  }

  abortController = new AbortController()
  loading.value = true

  try {
    const response = await api.get('/data', {
      signal: abortController.signal
    })
    data.value = response
  } catch (error) {
    if (error.name === 'CanceledError') {
      console.log('Request canceled')
    } else {
      console.error('Error:', error)
    }
  } finally {
    loading.value = false
  }
}

// 컴포넌트 언마운트 시 요청 취소
onBeforeUnmount(() => {
  if (abortController) {
    abortController.abort()
  }
})
</script>

<template>
  <div>
    <button @click="fetchData">Fetch Data</button>
    <div v-if="loading">Loading...</div>
    <div v-else-if="data">{{ data }}</div>
  </div>
</template>
```

## 🔁 재시도 로직

```javascript
// src/api/axios.js
import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api'
})

// 재시도 인터셉터
api.interceptors.response.use(null, async (error) => {
  const config = error.config

  // 재시도 설정이 없으면 초기화
  if (!config.__retryCount) {
    config.__retryCount = 0
  }

  // 최대 3번까지 재시도
  if (config.__retryCount >= 3) {
    return Promise.reject(error)
  }

  // 네트워크 에러나 5xx 에러만 재시도
  if (!error.response || error.response.status >= 500) {
    config.__retryCount++
    console.log(`Retry attempt ${config.__retryCount}`)

    // 1초 대기 후 재시도
    await new Promise(resolve => setTimeout(resolve, 1000))
    return api.request(config)
  }

  return Promise.reject(error)
})

export default api
```

## 📊 페이지네이션

```vue
<script setup>
import { ref, watch } from 'vue'
import { userAPI } from '@/api/user'

const users = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(0)
const loading = ref(false)

async function fetchUsers() {
  loading.value = true

  try {
    const data = await userAPI.getUsers({
      page: currentPage.value,
      limit: pageSize.value
    })

    users.value = data.users
    totalPages.value = data.totalPages
  } catch (error) {
    console.error('Failed to fetch users:', error)
  } finally {
    loading.value = false
  }
}

watch(currentPage, () => {
  fetchUsers()
})

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}
</script>

<template>
  <div>
    <div v-if="loading">Loading...</div>
    <ul v-else>
      <li v-for="user in users" :key="user.id">
        {{ user.name }}
      </li>
    </ul>

    <div>
      <button @click="prevPage" :disabled="currentPage === 1">Previous</button>
      <span>Page {{ currentPage }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">Next</button>
    </div>
  </div>
</template>
```

## 🔍 검색 및 필터링

```vue
<script setup>
import { ref, watch } from 'vue'
import { debounce } from 'lodash-es'
import { userAPI } from '@/api/user'

const searchQuery = ref('')
const users = ref([])
const loading = ref(false)

// 디바운스된 검색 함수
const debouncedSearch = debounce(async () => {
  if (!searchQuery.value) {
    users.value = []
    return
  }

  loading.value = true

  try {
    const data = await userAPI.getUsers({
      search: searchQuery.value
    })
    users.value = data.users
  } catch (error) {
    console.error('Search failed:', error)
  } finally {
    loading.value = false
  }
}, 300)

watch(searchQuery, () => {
  debouncedSearch()
})
</script>

<template>
  <div>
    <input 
      v-model="searchQuery" 
      type="text" 
      placeholder="Search users..."
    />

    <div v-if="loading">Searching...</div>
    <ul v-else-if="users.length">
      <li v-for="user in users" :key="user.id">
        {{ user.name }}
      </li>
    </ul>
    <div v-else-if="searchQuery">No results found</div>
  </div>
</template>
```

## 🔐 토큰 갱신

```javascript
// src/api/axios.js
import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api'
})

let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })

  failedQueue = []
}

api.interceptors.response.use(
  (response) => response.data,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // 토큰 갱신 중이면 대기열에 추가
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(token => {
          originalRequest.headers['Authorization'] = `Bearer ${token}`
          return api(originalRequest)
        }).catch(err => {
          return Promise.reject(err)
        })
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        const { token } = await axios.post('/api/auth/refresh', {
          refreshToken: localStorage.getItem('refreshToken')
        })

        localStorage.setItem('token', token)
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`
        processQueue(null, token)

        originalRequest.headers['Authorization'] = `Bearer ${token}`
        return api(originalRequest)
      } catch (err) {
        processQueue(err, null)
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        window.location.href = '/login'
        return Promise.reject(err)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  }
)

export default api
```

## 📚 실전 예제

### 통합 API 서비스

```javascript
// src/api/index.js
import api from './axios'

export const API = {
  // 인증
  auth: {
    login: (credentials) => api.post('/auth/login', credentials),
    logout: () => api.post('/auth/logout'),
    register: (userData) => api.post('/auth/register', userData),
  },

  // 사용자
  users: {
    list: (params) => api.get('/users', { params }),
    get: (id) => api.get(`/users/${id}`),
    create: (data) => api.post('/users', data),
    update: (id, data) => api.put(`/users/${id}`, data),
    delete: (id) => api.delete(`/users/${id}`),
  },

  // 게시글
  posts: {
    list: (params) => api.get('/posts', { params }),
    get: (id) => api.get(`/posts/${id}`),
    create: (data) => api.post('/posts', data),
    update: (id, data) => api.put(`/posts/${id}`, data),
    delete: (id) => api.delete(`/posts/${id}`),
  }
}

export default API
```

## 🔗 참고 자료

- [Axios 공식 문서](https://axios-http.com/)
- [Axios GitHub](https://github.com/axios/axios)
