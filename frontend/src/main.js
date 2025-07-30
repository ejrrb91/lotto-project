import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { VueQueryPlugin } from '@tanstack/vue-query'

//1. 현재 페이지의 URL에서 쿼리 파라미터를 가져옴
const urlParams = new URLSearchParams(window.location.search)
const accessToken = urlParams.get('accessToken')
const refreshToken = urlParams.get('refreshToken')

//2. accessToken과 refreshToken이 모두 존재할 경우
if (accessToken && refreshToken) {
  //3. Vue 앱이 로드되기 전에 localStorage에 토큰을 직접 저장
  localStorage.setItem('accessToken', accessToken)
  localStorage.setItem('refreshToken', refreshToken)

  //4. 주소창에서 토큰 파라미터를 제거
  window.history.replaceState({}, document.title, window.location.pathname)
}

const app = createApp(App)

//라우터를 사용하기 전에 Pinia를 먼저 use 합니다.
//라우터의 네비게이션 가드가 실행될 때,
// Pinia 스토어는 이미 localStorage의 값으로 초기화가 완료된 상태
app.use(createPinia())
app.use(router)
app.use(VueQueryPlugin)

app.mount('#app')
