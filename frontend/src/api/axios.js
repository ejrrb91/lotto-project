import axios from 'axios'
import { useAuthStore } from '@/stores/auth.js'

//axios 인스턴스 생성
const apiClient = axios.create({
  //baseURL: 'http://localhost:8080',
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

//응답 인터넵세(Response Interceptor) 설정
//모든 API 응답을 받은 후에 실행
apiClient.interceptors.response.use(
  (response) => {
    //성공적인 응답은 그대로 반환
    return response
  },
  async (error) => {
    const originalRequest = error.config
    const authStore = useAuthStore()

    //401 에러이고, 재시도한 요청이 아닐 경우 실행
    if (error.response.status === 401 && !originalRequest._retry) {
      //재시도 플래그 설정
      originalRequest._retry = true

      try {
        //저장된 refreshToken으로 새로운 accessToken 요청
        const response = await axios.post(
          'http://localhost:8080/api/users/reissue',
          {},
          { headers: { Authorization: `Bearer ${authStore.refreshToken}` } },
        )
        const { accessToken, refreshToken } = response.data

        //Pinia스토어와 localStorage에 새로운 토큰들을 저장
        authStore.setTokens(accessToken, refreshToken)

        //원래 실패했던 요청의 헤더에 새로운 accessToken을 설정하여 다시 보냄
        originalRequest.headers['Authorization'] = `Bearer ${accessToken}`
        return apiClient(originalRequest)
      } catch (reissueError) {
        //refreshToken 마저 만료되었거나 유효하지 않으면 로그아웃 처리
        authStore.logout()
        return Promise.reject(reissueError)
      }
    }
    //401 에러가 아니거나, 재시도에 실패한 경우 에러를 그대로 리턴
    return Promise.reject(error)
  },
)
export default apiClient
