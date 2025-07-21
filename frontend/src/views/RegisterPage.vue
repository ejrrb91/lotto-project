<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useMutation } from '@tanstack/vue-query'

const email = ref('')
const password = ref('')
const nickname = ref('')

const router = useRouter()
//useMutation을 사용해 '회원가입' 로직 정의
const { mutate, isPending } = useMutation({
  //mutationFn은 실제 비동기 작업을 수행하는 함수
  //axios를 이용해 회원가입 API를 호출
  mutationFn: (userData) => axios.post('http://localhost:8080/api/users/register', userData),
  //성공적으로 완료 되었을 때 실행
  onSuccess: () => {
    alert('회원가입에 성공했습니다. 로그인 페이지로 이동합니다.')
    router.push('/login')
  },
  //에러가 발생했을 때 실행
  onError: (error) => {
    console.error('회원가입 실패 : ', error.response.data)
    alert(error.response.data)
  },
})
//가입하기 버튼 눌렀을 때 실행되는 함수
const submitForm = () => {
  //useMutation에서 반환한 mutate 함수를 호출하여 API 요청을 시작
  //API에 보낼 데이터를 객체 형태로 전달
  mutate({
    email: email.value,
    password: password.value,
    nickname: nickname.value,
  })
}
</script>

<template>
  <div class="register-container">
    <h1>회원가입</h1>
    <form @submit.prevent="submitForm">
      <div class="form-group">
        <label for="email">이메일 : </label>
        <input type="email" id="email" v-model="email" required />
      </div>
      <div class="form-group">
        <label for="password">비밀번호 : </label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <div class="form-group">
        <label for="nickname">닉네임 : </label>
        <input type="text" id="nickname" v-model="nickname" required />
      </div>
      <button type="submit" :disabled="isPending">
        {{ isPending ? '가입 처리 중 입니다.' : '가입하기' }}
      </button>
    </form>
    <div class="login-link">
      이미 계정이 있으신가요?
      <router-link to="/login">로그인</router-link>
    </div>
  </div>
</template>

<style scoped>
.register-container {
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
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background-color: #369f70;
}

.login-link {
  margin-top: 20px;
  font-size: 14px;
  color: #555;
}

.login-link a {
  color: #007bff;
  text-decoration: none;
  font-weight: bold;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
