import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { jwtDecode } from 'jwt-decode'
import router from '@/router/index.js'
import apiClient from '@/api/axios.js'

//auth라는 이름의 store를 정의
export const useAuthStore = defineStore('auth', () => {
  //State : store에서 관리할 상태(데이터)
  //페이지를 새로고침해도 로그인 상태가 유지되도록 localStorage에서 초기값을 가져옴.
  const accessToken = ref(localStorage.getItem('accessToken'))
  const refreshToken = ref(localStorage.getItem('refreshToken'))
  const user = ref(null)

  // Getter (computed): state를 기반으로 하는 계산된 값
  // accessToken이 존재하면(null이나 빈 문자열이 아니면) true를 반환.
  const isLoggedIn = computed(() => !!accessToken.value)
  const userNickname = computed(() => user.value?.nickname || null)

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
    localStorage.setItem('accessToken', newRefreshToken)
  }

  async function logout() {
    if (confirm('정말 로그아웃 하시겠습니까?')) {
      try {
        //1. 백엔드에 로그아웃 요청을 보내 Redis의 RefreshToken 삭제를 시도
        await apiClient.post('/api/users/logout')
        console.log('서버 측 로그아웃 성공')
      } catch (error) {
        //2. 서버와의 통신이 실패하더라도 프론트엔드에서는 로그아웃 처리를 진행
        console.log('서버 측 로그아웃 실패')
      } finally {
        //3. API 호출 성공 여부와 관계없이 브라우저의 상태와 저장소는 정리
        accessToken.value = null
        refreshToken.value = null
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        sessionStorage.removeItem('chatUsername')
        alert('로그아웃 되었습니다.')
        router.push('/')
      }
    }
  }

  // Action: 로그아웃 시 토큰 상태와 user를 localStorage에서 모두 제거하는 함수
  function clearTokens() {
    accessToken.value = null
    user.value = null
    localStorage.removeItem('accessToken')
  }

  //토큰을 해독하여 사용자 정보를 user 상태에 저장하고, 만료 여부를 체크하는 함수
  function updateUserStateFromToken() {
    if (accessToken.value) {
      try {
        const decoded = jwtDecode(accessToken.value)
        //해독된 사용자 정보를 user 상태에 저장
        user.value = {
          nickname: decoded.nickname,
          exp: decoded.exp,
        }

        //토큰 만료 시간 확인
        const currentTime = Date.now() / 1000
        if (decoded.exp < currentTime) {
          //AccessToken이 만료되었을 뿐, 자동 재발급 로직이 처리하므로 여기서는 logout() 호출 X
        }
      } catch (error) {
        console.error('유효하지 않은 토큰입니다. 강제 로그아웃합니다.', error)
        logout()
      }
    } else {
      // 토큰이 없으면 사용자 정보도 비움
      user.value = null
    }
  }

  watch(accessToken, updateUserStateFromToken, { immediate: true })

  return {
    accessToken,
    refreshToken,
    isLoggedIn,
    userNickname,
    setTokens,
    logout,
  }
})
