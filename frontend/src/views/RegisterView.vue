<template>
  <div class="d-flex justify-content-center align-items-center vh-100 bg-light">
    <div class="card shadow-sm" style="width: 22rem;">
      <div class="card-body">
        <h3 class="text-center mb-4 text-primary">Cadastro</h3>

        <form @submit.prevent="handleRegister">
          <div class="mb-3">
            <label for="nome" class="form-label">Nome</label>
            <input
              v-model="nome"
              id="nome"
              type="text"
              class="form-control"
              placeholder="Seu nome"
              required
            />
          </div>

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

          <button type="submit" class="btn btn-primary w-100">Registrar</button>
        </form>
      </div>

      <div class="card-footer text-center bg-light">
        <small class="text-muted">Já tem conta?</small>
        <router-link
          to="/login"
          class="ms-1 text-decoration-none fw-semibold text-primary"
        >
          Faça login
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../store/auth'
import { useRouter } from 'vue-router'

const nome = ref('')
const email = ref('')
const senha = ref('')
const store = useAuthStore()
const router = useRouter()

async function handleRegister() {
  if (!nome.value.trim() || !email.value.trim() || !senha.value.trim()) {
    alert('Preencha o(s) campo(s) para prosseguir.')
    return
  }

  try {
    await store.register(nome.value.trim(), email.value.trim(), senha.value)
    alert('Usuário cadastrado! Aguarde aprovação.')
    router.push('/login')
  } catch {
    alert('Erro ao registrar.')
  }
}
</script>

<style scoped>
.card {
  border-radius: 10px;
}
</style>
