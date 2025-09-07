import { createRouter, createWebHistory } from 'vue-router'
import { parseJwt, deriveRole, routeForRole } from '../utils/auth'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/dashboard', component: () => import('../views/Dashboard.vue'), meta: { auth: true } },
  { path: '/items', component: () => import('../views/Items.vue'), meta: { auth: true } },
  { path: '/lost', component: () => import('../views/Lost.vue'), meta: { auth: true } },
]

const router = createRouter({ history: createWebHistory(), routes })

// basic auth guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken')
  const isAuthed = !!token

  if (to.meta?.auth && !isAuthed) {
    return next('/login')
  }

  if (to.path === '/login' && isAuthed) {
    const payload = parseJwt(token)
    const role = deriveRole(payload, null)
    return next(routeForRole(role))
  }

  next()
})

export default router
