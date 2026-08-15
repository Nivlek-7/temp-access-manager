import { defineStore } from 'pinia'
import axios from 'axios'
const API_URL = 'http://localhost:8080/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({ 
    token: localStorage.getItem('token') || null,
    role: localStorage.getItem('role') || null
  }),
  getters: { isAuthenticated: (state) => !!state.token },
  actions: {
    async login(email, senha) {
      const response = await axios.post(`${API_URL}/auth/login`, { email, senha })
      this.token = response.data.token
      this.role = response.data.role
      localStorage.setItem('token', this.token)
      localStorage.setItem('role', this.role)
      axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
    },
    async register(nome, email, senha) {
      await axios.post(`${API_URL}/auth/registrar`, { nome, email, senha })
    },
    logout() {
      this.token = null
      this.role = null
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      delete axios.defaults.headers.common['Authorization']
    },
    async listarUsuariosPendentes() {
      const { data: usuariosPendentes } = await axios.get(`${API_URL}/usuario/pendentes`, {
        headers: { Authorization: `Bearer ${this.token}` }
      })
      return usuariosPendentes
    },
    async aprovarUsuario(userId) {
      await axios.post(`${API_URL}/usuario/aprovar/${userId}`, null, {
        headers: { Authorization: `Bearer ${this.token}` }
      })
    },
    async rejeitarUsuario(userId) {
      await axios.post(`${API_URL}/usuario/rejeitar/${userId}`, null, {
        headers: { Authorization: `Bearer ${this.token}` }
      })
    },
    
    async listarUsuarios() { // lista usuarios do tipo USER apenas
      const response = await axios.get(`${API_URL}/usuario`, {
        headers: { Authorization: `Bearer ${this.token}` }
      })
      return response.data
    },

    async listarAcessosUsuario() {  // lista apenas os acessos do usuário logado
      const response = await axios.get(`${API_URL}/acesso/usuario`, {
        headers: { Authorization: `Bearer ${this.token}` }
      })
      return response.data
    },

    async listarAcessos() {
      const response = await axios.get(`${API_URL}/acesso`, {
        headers: { Authorization: `Bearer ${this.token}` }
      })
      return response.data
    },
    async permitirAcesso(usuarioId, nomeRecurso, duracaoSegundos) {
      const response = await axios.post(`${API_URL}/acesso/permitir`, { usuarioId, nomeRecurso, duracaoSegundos }, {
        headers: { Authorization: `Bearer ${this.token}` }
      })
      return response.data
    },
    async revogarAcesso(acessoId) {
      await axios.post(`${API_URL}/acesso/revogar/${acessoId}`, null, {
        headers: { Authorization: `Bearer ${this.token}` }
      })
    }
  }
})
