import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import axios from 'axios'
import { useAuthStore } from './auth'

vi.mock('axios', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    defaults: { headers: { common: {} } }
  }
}))

function tokenComExpiracao(exp) {
  return `cabecalho.${btoa(JSON.stringify({ exp })).replace(/=/g, '')}.assinatura`
}

describe('auth store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
    vi.useRealTimers()
  })

  it('realiza login e persiste a sessão', async () => {
    const token = tokenComExpiracao(Math.floor(Date.now() / 1000) + 3600)
    axios.post.mockResolvedValue({ data: { token, role: 'ADMIN' } })
    const store = useAuthStore()

    await store.login('admin@example.com', 'senha')

    expect(axios.post).toHaveBeenCalledWith('/api/auth/login', {
      email: 'admin@example.com', senha: 'senha'
    })
    expect(store.role).toBe('ADMIN')
    expect(localStorage.getItem('token')).toBe(token)
    expect(axios.defaults.headers.common.Authorization).toBe(`Bearer ${token}`)
  })

  it('propaga falha de login sem criar sessão', async () => {
    axios.post.mockRejectedValue(new Error('credenciais inválidas'))
    const store = useAuthStore()

    await expect(store.login('user@example.com', 'errada')).rejects.toThrow('credenciais inválidas')
    expect(store.isAuthenticated).toBe(false)
  })

  it('envia os dados de registro', async () => {
    axios.post.mockResolvedValue({})
    const store = useAuthStore()

    await store.register('Maria', 'maria@example.com', 'senha')

    expect(axios.post).toHaveBeenCalledWith('/api/auth/registrar', {
      nome: 'Maria', email: 'maria@example.com', senha: 'senha'
    })
  })

  it('faz logout quando o token expira', () => {
    vi.useFakeTimers()
    vi.setSystemTime(new Date('2026-08-18T12:00:00Z'))
    const store = useAuthStore()
    store.token = tokenComExpiracao(Math.floor(Date.now() / 1000) + 10)
    store.role = 'USER'
    localStorage.setItem('token', store.token)
    localStorage.setItem('role', store.role)

    store.scheduleLogout()
    vi.advanceTimersByTime(10_000)

    expect(store.isAuthenticated).toBe(false)
    expect(localStorage.getItem('token')).toBeNull()
  })
})
