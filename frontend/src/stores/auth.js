import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

//auth라는 이름의 store를 정의
export const useAuthStore = defineStore('auth', () => {
  //state : store에서 관리할 상태(데이터)
  const accessToken = ref(localStorage.getItem('accessToken') || null)
  const refreshToken = ref(localStorage.getItem('refreshToken') || null)

  //getters: state를 기반으로 하는 계산된 값
  const isLoggedIn = computed(() => !!accessToken.value)

  //actions: state를 변경하는 메서드
  function setTokens(newAccessToken, newRefreshToken) {
    accessToken.value = newAccessToken
    refreshToken.value = newRefreshToken
    localStorage.setItem('accessToken', newAccessToken)
    localStorage.setItem('refreshToken', newRefreshToken)
  }

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
