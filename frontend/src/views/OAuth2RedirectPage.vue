<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
  //1. URL의 쿼리 파라미터에서 토큰을 추출
  const accessToken = route.query.accessToken
  const refreshToken = route.query.refreshToken

  if (accessToken && refreshToken) {
    //2. 토큰이 존재하면, Pinia 스토어와 Local Storage에 저장
    authStore.setTokens(accessToken, refreshToken)

    //3. 메인페이지로 리디렉션
    // SPA 라우팅 대신, 페이지를 완전히 새로고침하여 이동.
    //replace를 사용하면 브라우저 히스토리에 이 페이지가 남지 않음
    router.replace('/')
  } else {
    //4. 토큰이 없을 경우 로그인 페이지로 이동
    console.error('소셜 로그인 실패, 토큰이 없습니다.')
    alert('로그인에 실패했습니다. 다시 시도해 주세요.')
    router.replace('/login')
  }
})
</script>

<template>
  <div class="redirect-container">
    <p>로그인 중입니다. 잠시만 기다려 주세요.</p>
  </div>
</template>

<style scoped>
.redirect-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 1.2rem;
}
</style>
