<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="logo-section">
        <h1>{{ isLogin ? "로그인" : "회원가입" }}</h1>
        <p class="subtitle">PSMO Community</p>
      </div>

      <form @submit.prevent="handleSubmit">
        <div v-if="!isLogin" class="form-group">
          <label>이름</label>
          <input
            type="text"
            v-model="formData.name"
            placeholder="이름을 입력하세요"
            required
          />
        </div>

        <div class="form-group">
          <label>이메일</label>
          <input
            type="email"
            v-model="formData.email"
            placeholder="example@email.com"
            required
          />
        </div>

        <div class="form-group">
          <label>비밀번호</label>
          <input
            type="password"
            v-model="formData.password"
            placeholder="비밀번호를 입력하세요"
            required
          />
        </div>

        <div v-if="!isLogin" class="form-group">
          <label>비밀번호 확인</label>
          <input
            type="password"
            v-model="formData.passwordConfirm"
            placeholder="비밀번호를 다시 입력하세요"
            required
          />
        </div>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? "처리중..." : isLogin ? "로그인" : "회원가입" }}
        </button>
      </form>

      <div class="auth-footer">
        <button @click="toggleMode" class="btn-toggle">
          {{ isLogin ? "회원가입 하기" : "로그인 하기" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { authApi } from "@/api";
import { useAuthStore } from "@/stores/auth";
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const isLogin = ref(true);
const loading = ref(false);
const errorMessage = ref("");

const formData = reactive({
  name: "",
  email: "",
  password: "",
  passwordConfirm: "",
});

function toggleMode() {
  isLogin.value = !isLogin.value;
  errorMessage.value = "";
  formData.name = "";
  formData.email = "";
  formData.password = "";
  formData.passwordConfirm = "";
}

async function handleSubmit() {
  errorMessage.value = "";

  // 회원가입 시 비밀번호 확인
  if (!isLogin.value && formData.password !== formData.passwordConfirm) {
    errorMessage.value = "비밀번호가 일치하지 않습니다.";
    return;
  }

  loading.value = true;

  try {
    let response;
    if (isLogin.value) {
      // 로그인
      response = await authApi.login(formData.email, formData.password);
    } else {
      // 회원가입
      response = await authApi.register(
        formData.name,
        formData.email,
        formData.password
      );
    }

    const { user, token, refreshToken } = response.data;
    authStore.login(user, token, refreshToken);

    // 리다이렉트 URL이 있으면 해당 페이지로, 없으면 홈으로
    const redirectPath = route.query.redirect || "/";
    router.push(redirectPath);
  } catch (error) {
    console.error("Auth error:", error);
    errorMessage.value =
      error.response?.data?.message ||
      (isLogin.value ? "로그인에 실패했습니다." : "회원가입에 실패했습니다.");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.auth-container {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 1rem;
}

.auth-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.logo-section {
  text-align: center;
  margin-bottom: 2rem;
}

.logo-section h1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #666;
  font-size: 0.9rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #2c3e50;
  font-size: 0.9rem;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 0.75rem;
  border-radius: 6px;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  text-align: center;
}

.btn-submit {
  width: 100%;
  padding: 0.875rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.3s;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-submit:active:not(:disabled) {
  opacity: 0.8;
}

.auth-footer {
  margin-top: 1.5rem;
  text-align: center;
}

.btn-toggle {
  background: none;
  border: none;
  color: #667eea;
  font-size: 0.9rem;
  cursor: pointer;
  text-decoration: underline;
}

.btn-toggle:active {
  opacity: 0.7;
}
</style>
