import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import LoginView from './LoginView.vue'
import RegisterView from './RegisterView.vue'

const { store, push } = vi.hoisted(() => ({
  store: { login: vi.fn(), register: vi.fn(), role: null },
  push: vi.fn()
}))

vi.mock('../store/auth', () => ({ useAuthStore: () => store }))
vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
  RouterLink: { template: '<a><slot /></a>' }
}))

async function preencherLogin(wrapper) {
  await wrapper.get('#email').setValue('user@example.com')
  await wrapper.get('#senha').setValue('senha')
  await wrapper.get('form').trigger('submit')
  await flushPromises()
}

describe('telas de autenticação', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    store.role = null
    vi.stubGlobal('alert', vi.fn())
  })

  it.each([
    ['ADMIN', '/dashboard'],
    ['USER', '/home']
  ])('redireciona login %s para %s', async (role, destino) => {
    store.role = role
    store.login.mockResolvedValue()
    const wrapper = mount(LoginView, { global: { stubs: { RouterLink: true } } })

    await preencherLogin(wrapper)

    expect(store.login).toHaveBeenCalledWith('user@example.com', 'senha')
    expect(push).toHaveBeenCalledWith(destino)
  })

  it('renderiza o erro devolvido pela API no login', async () => {
    store.login.mockRejectedValue({ response: { data: { detail: 'Credenciais inválidas.' } } })
    const wrapper = mount(LoginView, { global: { stubs: { RouterLink: true } } })

    await preencherLogin(wrapper)

    expect(wrapper.get('[role="alert"]').text()).toBe('Credenciais inválidas.')
    expect(push).not.toHaveBeenCalled()
  })

  it('registra e redireciona para o login', async () => {
    store.register.mockResolvedValue()
    const wrapper = mount(RegisterView, { global: { stubs: { RouterLink: true } } })
    await wrapper.get('#nome').setValue(' Maria ')
    await wrapper.get('#email').setValue(' maria@example.com ')
    await wrapper.get('#senha').setValue('senha')

    await wrapper.get('form').trigger('submit')
    await flushPromises()

    expect(store.register).toHaveBeenCalledWith('Maria', 'maria@example.com', 'senha')
    expect(push).toHaveBeenCalledWith('/login')
  })

  it('renderiza o erro devolvido pela API no registro', async () => {
    store.register.mockRejectedValue({ response: { data: { detail: 'E-mail já cadastrado.' } } })
    const wrapper = mount(RegisterView, { global: { stubs: { RouterLink: true } } })
    await wrapper.get('#nome').setValue('Maria')
    await wrapper.get('#email').setValue('maria@example.com')
    await wrapper.get('#senha').setValue('senha')

    await wrapper.get('form').trigger('submit')
    await flushPromises()

    expect(wrapper.get('[role="alert"]').text()).toBe('E-mail já cadastrado.')
  })
})
