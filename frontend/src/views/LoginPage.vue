<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMutation } from '@tanstack/vue-query'
import { useAuthStore } from '@/stores/auth.js'
import apiClient from '@/api/axios.js'

const email = ref('')
const password = ref('')
const router = useRouter()
const authStore = useAuthStore()

const { mutate, isPending } = useMutation({
  mutationFn: (loginData) => apiClient.post('/api/users/login', loginData),
  onSuccess: (response) => {
    const { accessToken, refreshToken } = response.data
    //로그인 성공 시, authStore에 토큰 저장
    authStore.setTokens(accessToken, refreshToken)

    alert('로그인에 성공했습니다.\n 홈 페이지로 이동합니다.')
    router.push('/')
  },
  onError: (error) => {
    console.error('로그인 실패 : ', error.response.data)
    alert(error.response.data.message || '로그인에 실패했습니다.')
  },
})

const submitForm = () => {
  mutate({
    email: email.value,
    password: password.value,
  })
}
</script>

<template>
  <div class="login-container">
    <h2>로그인</h2>
    <form @submit.prevent="submitForm" class="login-form">
      <div class="form-group">
        <label for="email">이메일</label>
        <input type="email" id="email" v-model="email" required />
      </div>
      <div class="form-group">
        <label for="password">비밀번호</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button type="submit" :disabled="isPending">
        {{ isPending ? '로그인 중...' : '로그인' }}
      </button>
    </form>
    <div class="social-login-divider">
      <span>또는</span>
    </div>

    <a href="/oauth2/authorization/google" class="google-login-btn">
      <img src="/google-logo.svg" alt="Google logo" />
      <span>Login with Google</span>
    </a>
    <div class="register-link">
      계정이 없으신가요?
      <router-link to="/register">회원가입</router-link>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box; /* padding이 너비에 포함되도록 설정 */
}

button {
  width: 100%;
  padding: 10px;
  background-color: #42b983; /* 추천 결과 색상과 통일 */
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background-color: #36a473;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* [추가된 스타일] */
.social-login-divider {
  display: flex;
  align-items: center;
  text-align: center;
  color: #aaa;
  margin: 20px 0;
}

.social-login-divider::before,
.social-login-divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #ccc;
}

.social-login-divider:not(:empty)::before {
  margin-right: 0.25em;
}

.social-login-divider:not(:empty)::after {
  margin-left: 0.25em;
}

.google-login-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #fff;
  cursor: pointer;
  text-decoration: none;
  color: #333;
  font-weight: bold;
  transition: background-color 0.3s;
  box-sizing: border-box; /* padding이 너비에 포함되도록 설정 */
}

.google-login-btn:hover {
  background-color: #f5f5f5;
}

.google-login-btn img {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}

.register-link {
  margin-top: 20px;
  font-size: 14px;
  color: #555;
}

.register-link a {
  color: #007bff;
  text-decoration: none;
  font-weight: bold;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
