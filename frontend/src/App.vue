<script setup>
import { RouterView, useRoute, useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import { useAuthStore } from '@/stores/auth.js'
import { onMounted } from 'vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
  //URL 쿼리 파라미터에서 토큰 확인
  const accessToken = route.query.accessToken
  const refreshToken = route.query.refreshToken

  if (accessToken && refreshToken) {
    //토큰이 있을 경우 저장소에 저장
    authStore.setTokens(accessToken, refreshToken)
    //URL에서 토큰 정보 제거
    router.replace({ query: {} })
    //페이지를 새로고침하여 로그인 상태 반영
    window.location.reload()
  }
})
</script>

<template>
  <Header />
  <RouterView />
</template>

<style>
body {
  margin: 0;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

main {
  padding: 20px;
}
</style>
