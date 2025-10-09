<template>
  <Navbar />
  <div class="container mt-5">
    <h3 class="mb-4 text-primary">Usuários Pendentes</h3>

    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th scope="col" style="width: 15%">Ação</th>
            <th scope="col" style="width: 25%">Nome</th>
            <th scope="col" style="width: 40%">E-mail</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="usuario in usuariosPendentes" :key="usuario.id">
            <td>
              <button
                class="btn btn-success btn-sm mx-1"
                @click="aprovarUsuario(usuario.id)"
              >
                <i class="bi bi-check-circle me-1"></i> Aprovar
              </button>
              <button
                class="btn btn-danger btn-sm"
                @click="rejeitarUsuario(usuario.id)"
              >
                <i class="bi bi-x-circle me-1"></i> Rejeitar
              </button>
            </td>
            <td>{{ usuario.nome }}</td>
            <td>{{ usuario.email }}</td>
          </tr>

          <tr v-if="usuariosPendentes.length === 0">
            <td colspan="3" class="text-center text-muted py-4">
              Nenhum usuário pendente.
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../store/auth'
import Navbar from '../components/Navbar.vue'
const store = useAuthStore()
const usuariosPendentes = ref([])

async function carregarUsuariosPendentes() {
  try {
    usuariosPendentes.value = await store.listarUsuariosPendentes()
  } catch {
    alert('Erro ao carregar usuários pendentes.')
  }
}
async function aprovarUsuario(usuarioId) {
  try {
    await store.aprovarUsuario(usuarioId)
    usuariosPendentes.value = usuariosPendentes.value.filter(u => u.id !== usuarioId)
    alert('Usuário aprovado com sucesso.')
  } catch {
    alert('Erro ao aprovar o usuário.')
  }
}
async function rejeitarUsuario(usuarioId) {
  try {
    await store.rejeitarUsuario(usuarioId)
    usuariosPendentes.value = usuariosPendentes.value.filter(u => u.id !== usuarioId)
    alert('Usuário rejeitado com sucesso.')
  } catch {
    alert('Erro ao rejeitar o usuário.')
  }
}
onMounted(carregarUsuariosPendentes)
</script>

<style scoped>
.table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

h3 {
  font-weight: 600;
}

[data-bs-theme='dark'] .table {
  background-color: var(--bs-dark-bg-subtle);
  color: var(--bs-light);
}

[data-bs-theme='dark'] .table-light {
  background-color: #2a2a2a !important;
  color: #fff !important;
} 
</style>
