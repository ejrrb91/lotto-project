import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { VueQueryPlugin } from '@tanstack/vue-query'

const app = createApp(App)

//라우터를 사용하기 전에 Pinia를 먼저 use 합니다.
//라우터의 네비게이션 가드가 실행될 때,
// Pinia 스토어는 이미 localStorage의 값으로 초기화가 완료된 상태
app.use(createPinia())
app.use(router)
app.use(VueQueryPlugin)

app.mount('#app')
