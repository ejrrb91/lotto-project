import { createRouter, createWebHistory } from 'vue-router'
import Homepage from '@/views/HomePage.vue'
import LoginPage from '@/views/LoginPage.vue'
import RegisterPage from '@/views/RegisterPage.vue'
import MyPage from '@/views/MyPage.vue'
import HomePage from '@/views/HomePage.vue'
import { useAuthStore } from '@/stores/auth.js'
import OAuth2RedirectPage from '@/views/OAuth2RedirectPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterPage,
    },
    {
      path: '/oauth2/redirect',
      name: 'oauth-redirect',
      component: OAuth2RedirectPage,
    },
    {
      path: '/my-page',
      name: 'my-page',
      component: MyPage,
    },
  ],
})

router.beforeEach((to, from, next) => {
  // Pinia 스토어를 거치지 않고, 브라우저의 localStorage에서 직접 토큰을 가져옵니다.
  const token = localStorage.getItem('accessToken')

  const publicPages = ['/', '/login', '/register', '/oauth2/redirect']
  const authRequired = !publicPages.includes(to.path)

  // 로그인이 필요한 페이지에 접근하는데, 토큰이 없을 경우에만 로그인 페이지로 보냅니다.
  if (to.path === '/my-page' && !token) {
    alert('로그인이 필요합니다.')
    return next('/login')
  }

  next() // 그 외의 모든 경우는 통과시킵니다.
})

export default router
