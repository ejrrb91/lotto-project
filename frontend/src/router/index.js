import { createRouter, createWebHistory } from 'vue-router'
import Homepage from '@/views/Homepage.vue'
import LoginPage from '@/views/LoginPage.vue'
import RegisterPage from '@/views/RegisterPage.vue'
import OAuthRedirectPage from '@/views/OAuthRedirectPage.vue'
import MyPage from '@/views/MyPage.vue'
import { useAuthStore } from '@/stores/auth.js'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Homepage,
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
      path: '/oauth-redirect',
      name: 'oauth-redirect',
      component: OAuthRedirectPage,
    },
    {
      path: '/my-page',
      name: 'my-page',
      component: MyPage,
    },
  ],
})

/*
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const publicPages = ['/login', '/register', 'oauth-redirect']
  const authRequired = !publicPages.includes(to.path)

  //로그인이 필요한 페이지에 접근할 경우
  if (authRequired && !authStore.isLoggedIn) {
    //로그인이 안되어 있을 경우
    alert('로그인이 필요합니다.')
    return next('/login')
  }

  //그 외의 경우는 그대로 통과
  next()
})
 */

export default router
