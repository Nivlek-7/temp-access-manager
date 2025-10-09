import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import AccessListView from '../views/AccessListView.vue'
import HomeUserView from '../views/HomeUserView.vue'
import { useAuthStore } from '../store/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: LoginView },
    { path: '/register', component: RegisterView },
    { path: '/dashboard', component: DashboardView, meta: { requiresAuth: true } },
    { path: '/access', component: AccessListView, meta: { requiresAuth: true } },
    { path: '/home', component: HomeUserView, meta: { requiresAuth: true }}
  ]
})

router.beforeEach((to, from, next) => {
  const store = useAuthStore()

  if (to.meta.requiresAuth && !store.isAuthenticated) {
    return next('/login')
  }

  if (store.role === 'USER' && (to.path === '/dashboard' || to.path === '/access')) {
    return next('/home')
  }

  next()
})

export default router