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
    <h1>로그인</h1>
    <form @submit.prevent="submitForm">
      <div class="form-group">
        <label for="email">이메일 : </label>
        <input type="email" id="email" v-model="email" required />
      </div>
      <div class="form-group">
        <label for="password">비밀번호 : </label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button type="submit" :disabled="isPending">
        {{ isPending ? '로그인 중' : '로그인' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
/* 회원가입 페이지와 동일한 스타일을 사용합니다. */
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
  box-sizing: border-box;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background-color: #45a049;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
