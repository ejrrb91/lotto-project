<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import { computed, onMounted } from 'vue'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

//Pinia 스토어의 isLoggedIn 상태를 실시간으로 반영하기 위해 computed 속성 사용
const isLoggedIn = computed(() => authStore.isLoggedIn)
const nickname = computed(() => authStore.userNickname)

//현재 페이지가 로그인 또는 회원가입 페이지인지 확인
const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})

//로그아웃
const logout = () => {
  authStore.logout() //스토어의 로그아웃 액션 호출
}
</script>

<template>
  <header class="main-header">
    <nav>
      <router-link to="/" class="logo">Lotto Helper</router-link>
      <div class="nav-links">
        <template v-if="isLoggedIn">
          <span class="welcome-message">{{ nickname }}님</span>
          <router-link to="/my-page">마이 페이지</router-link>
          <a @click="logout" href="#" class="logout-btn">로그아웃</a>
        </template>
        <template v-else-if="!isAuthPage">
          <router-link to="/login">로그인</router-link>
          <router-link to="/register">회원가입</router-link>
        </template>
      </div>
    </nav>
  </header>
</template>

<style scoped>
.main-header {
  padding: 10px 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  font-weight: bold;
  font-size: 24px;
  text-decoration: none;
  color: #42b983;
}

.nav-links {
  display: flex;
  gap: 20px;
}

.nav-links a {
  text-decoration: none;
  color: #333;
  cursor: pointer;
}

.nav-links a:hover {
  text-decoration: underline;
}

.welcome-message {
  color: #333;
  font-weight: bold;
}

.logout-btn {
  color: #dc3545;
}
</style>
