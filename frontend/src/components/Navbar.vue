<template>
  <nav class="navbar navbar-expand-lg bg-body-tertiary shadow-sm">
    <div class="container-fluid">
      <a class="navbar-brand fw-bold" href="#">Painel</a>

      <button 
        class="navbar-toggler" 
        type="button" 
        data-bs-toggle="collapse" 
        data-bs-target="#navbarNav"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <template v-if="role === 'ADMIN'">
            <li class="nav-item">
              <RouterLink to="/dashboard" class="nav-link">Início</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/dashboard" class="nav-link">Usuários</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/access" class="nav-link">Acessos</RouterLink>
            </li>
          </template>

          <template v-else-if="role === 'USER'">
            <li class="nav-item">
              <RouterLink to="/home" class="nav-link">Início</RouterLink>
            </li>
          </template>
        </ul>

        <button 
          class="btn btn-outline-secondary d-flex align-items-center mx-1 equal-btn"
          @click="themeStore.toggleTheme"
        >
          <i v-if="themeStore.theme === 'light'" class="bi bi-moon-fill me-1"></i>
          <i v-else class="bi bi-sun-fill me-1"></i>
          {{ themeStore.theme === 'light' ? 'Modo Escuro' : 'Modo Claro' }}
        </button>

        <button
          class="btn btn-outline-danger d-flex align-items-center justify-content-center equal-btn"
          @click="logout"
        >
          <i class="bi bi-box-arrow-right me-2"></i>
          <span>Sair</span>
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useThemeStore } from '../store/theme'
import { useAuthStore } from '../store/auth'
import { useRouter } from 'vue-router'

const themeStore = useThemeStore()
const store = useAuthStore()
const router = useRouter()

const role = localStorage.getItem('role')

function logout() {
  store.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  transition: background-color 0.3s ease, color 0.3s ease;
}

.equal-btn {
  height: 40px;
  display: flex;
  align-items: center;
}
</style>