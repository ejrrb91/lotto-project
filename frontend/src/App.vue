<script setup>
import { RouterView, useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import { useAuthStore } from '@/stores/auth.js'
import { onMounted } from 'vue'

const route = useRoute()
const authStore = useAuthStore()

onMounted(() => {
  const accessToken = route.query.accessToken
  const refreshToken = route.query.refreshToken

  if (accessToken && refreshToken) {
    //1. 토큰을 localStorage에 먼저 저장합니다.
    authStore.setTokens(accessToken, refreshToken)

    //2. 토큰 정보가 없는 페이지 주소로 브라우저를 이동시킴
    //히스토리에 토큰 정보가 남지 않음
    window.location.href = '/'
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
