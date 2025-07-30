<script setup>
import { RouterView, useRoute, useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import { useAuthStore } from '@/stores/auth.js'
import { onMounted, watch } from 'vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

onMounted(async () => {
  console.log('[Lotto Helper 디버그] App.vue가 마운트되었습니다.')
  //URL 쿼리 파라미터에서 토큰 확인
  const accessToken = route.query.accessToken
  const refreshToken = route.query.refreshToken
  console.log('[Lotto Helper 디버그] URL에서 토큰을 읽습니다...')
  console.log('[Lotto Helper 디버그] Access Token:', accessToken || '없음')
  console.log('[Lotto Helper 디버그] Refresh Token:', refreshToken || '없음')

  if (accessToken && refreshToken) {
    console.log('[Lotto Helper 디버그] URL에서 토큰 발견! 저장을 시도합니다.')

    //토큰이 있을 경우 저장소에 저장
    authStore.setTokens(accessToken, refreshToken)
    const savedToken = localStorage.getItem('accessToken')
    if (savedToken) {
      console.log('[Lotto Helper 디버그] authStore.setTokens 함수가 호출되었습니다.')
    } else {
      console.error('[Lotto Helper 디버그] 실패: LocalStorage에 토큰이 저장되지 않았습니다!')
    }

    //URL에서 토큰 정보 제거
    await router.replace({ query: {} })
    console.log('[Lotto Helper 디버그] URL을 정리했습니다.')
    //페이지를 새로고침하여 로그인 상태 반영
    // window.location.reload()
    alert(
      '디버깅 모드: 페이지 새로고침을 중단했습니다. 개발자 도구의 Console과 Application 탭을 확인해주세요.',
    )
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
