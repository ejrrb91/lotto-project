import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

//auth라는 이름의 store를 정의
export const useAuthStore = defineStore('auth', () => {
  //State : store에서 관리할 상태(데이터)
  //페이지를 새로고침해도 로그인 상태가 유지되도록 localStorage에서 초기값을 가져옴.
  const accessToken = ref(localStorage.getItem('accessToken'))
  const refreshToken = ref(localStorage.getItem('refreshToken'))

  // Getter (computed): state를 기반으로 하는 계산된 값
  // accessToken이 존재하면(null이나 빈 문자열이 아니면) true를 반환.
  const isLoggedIn = computed(() => !!accessToken.value)

  // useRouter는 <script setup>이나 setup() 함수 안에서만 사용 가능하므로,
  // 필요하다면 사용하는 컴포넌트에서 직접 호출.
  // const router = useRouter();

  //Actions: 토큰을 상태와 localStorage에 저장하는 함수
  function setTokens(newAccessToken, newRefreshToken) {
    // Pinia의 실시간 상태(state)를 업데이트
    accessToken.value = newAccessToken
    refreshToken.value = newRefreshToken
    // 새로고침을 위해 localStorage에도 저장.
    localStorage.setItem('accessToken', newAccessToken)
    localStorage.setItem('refreshToken', newRefreshToken)
  }

  // Action: 로그아웃 시 토큰을 상태와 localStorage에서 모두 제거하는 함수
  function clearTokens() {
    accessToken.value = null
    refreshToken.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  return {
    accessToken,
    refreshToken,
    isLoggedIn,
    setTokens,
    clearTokens,
  }
})
