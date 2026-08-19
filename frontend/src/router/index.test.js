import { beforeEach, describe, expect, it } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../store/auth'
import { authGuard } from './index'

describe('proteção de rotas', () => {
  beforeEach(() => {
    localStorage.clear()
    setActivePinia(createPinia())
  })

  it('redireciona visitante para o login', () => {
    expect(authGuard({ path: '/dashboard', meta: { requiresAuth: true } })).toBe('/login')
  })

  it('impede USER de acessar rotas administrativas', () => {
    const store = useAuthStore()
    store.token = 'token'
    store.role = 'USER'

    expect(authGuard({ path: '/dashboard', meta: { requiresAuth: true } })).toBe('/home')
    expect(authGuard({ path: '/access', meta: { requiresAuth: true } })).toBe('/home')
  })

  it('permite ADMIN em rota administrativa', () => {
    const store = useAuthStore()
    store.token = 'token'
    store.role = 'ADMIN'

    expect(authGuard({ path: '/dashboard', meta: { requiresAuth: true } })).toBeUndefined()
  })
})
