<script setup>
import { RouterView, useRoute, useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import { useAuthStore } from '@/stores/auth.js'
import { onMounted, watch } from 'vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const handleTokenFromUrl = () => {
  console.log('1. handleTokenFromUrl 함수 실행, 현재 URL 쿼리 : ', route.query)

  //URL 쿼리 파라미터에서 토큰 확인
  const accessToken = route.query.accessToken
  const refreshToken = route.query.refreshToken

  if (accessToken && refreshToken) {
    console.log('2. AccessToken과 RefreshToken을 URL에서 확인')

    //토큰이 있을 경우 저장소에 저장
    authStore.setTokens(accessToken, refreshToken)
    console.log('3. 토큰을 LocalStorage에 저장')

    //URL에서 토큰 정보 제거
    router.replace({ query: {} })
    console.log('4. URL에서 토큰을 제거')

    alert('로그인 성공, 페이지 새로고침')
    //페이지를 새로고침하여 로그인 상태 반영
    window.location.reload()
  } else {
    console.log('URL에서 토큰을 찾지 못했습니다.(정상적인 페이지 로드)')
  }
}
onMounted(() => {
  console.log('App.vue 마운트')
  handleTokenFromUrl()
})
//라우트의 쿼리가 변경될 때를 감지하여 한번 더 실행(타이밍 이슈 해결)
watch(
  () => route.query,
  (newQuery) => {
    console.log('URL 쿼리 변경 : ', newQuery)
    handleTokenFromUrl()
  },
  {
    deep: true,
    immediate: false,
  },
)
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
