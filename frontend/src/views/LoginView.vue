<template>
  <div class="d-flex justify-content-center align-items-center vh-100 bg-light">
    <div class="card shadow-sm" style="width: 22rem;">
      <div class="card-body">
        <h3 class="text-center mb-4 text-primary">Login</h3>
        <div v-if="erro" class="alert alert-danger" role="alert">{{ erro }}</div>

        <form @submit.prevent="handleLogin">
          <div class="mb-3">
            <label for="email" class="form-label">E-mail</label>
            <input
              v-model="email"
              id="email"
              type="email"
              class="form-control"
              placeholder="exemplo@email.com"
              required
            />
          </div>

          <div class="mb-3">
            <label for="senha" class="form-label">Senha</label>
            <input
              v-model="senha"
              id="senha"
              type="password"
              class="form-control"
              placeholder="••••"
              required
            />
          </div>

          <button type="submit" class="btn btn-primary w-100">Entrar</button>
        </form>
      </div>

      <div class="card-footer text-center bg-light">
        <small class="text-muted">Ainda não tem uma conta?</small>
        <router-link
          to="/register"
          class="ms-1 text-decoration-none fw-semibold text-primary"
        >
          Criar conta
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../store/auth'
import { useRouter } from 'vue-router'

const email = ref('')
const senha = ref('')
const router = useRouter()
const store = useAuthStore()
const erro = ref('')

async function handleLogin() {
  erro.value = ''
  try {
    await store.login(email.value, senha.value)

    if (store.role === 'ADMIN') {
      router.push('/dashboard')
    } else if (store.role === 'USER') {
      router.push('/home')
    } else {
      alert('Tipo de usuário desconhecido!')
    }

  } catch (error) {
    erro.value = error.response?.data?.detail || 'Erro ao autenticar.'
  }
}
</script>

<style scoped>
.card {
  border-radius: 10px;
}
</style>
