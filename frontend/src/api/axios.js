import axios from 'axios'
import { useAuthStore } from '@/stores/auth.js'

//axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
})

//요청 인터셉터(Request Interceptor) 설정
//모든 API 요청이 보내지기 전에 이 코드가 먼저 실행
apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const token = authStore.accessToken

    if (token) {
      //모든 요청의 Authorization 헤더에 Bearer 토큰을 추가
      config.headers['Authorization'] = `Bearer ${token}`
    }
    //수정된 config를 반환하여 요청을 계속 진행
    return config
  },
  (error) => {
    //요청 에러 처리
    return Promise.reject(error)
  },
)

export default apiClient
