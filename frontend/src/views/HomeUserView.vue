<template>
  <Navbar />
  <div class="container mt-4">
    <h3 class="mb-3">Meus Acessos</h3>

    <table class="table table-striped table-hover text-center">
      <thead class="table-light">
        <tr>
          <th>Nome do Recurso</th>
          <th>Data Inicial</th>
          <th>Data Final</th>
          <th>Tempo Restante</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="acesso in acessos" :key="acesso.id" :class="getTempoClasse(acesso)">
          <td>{{ acesso.nomeRecurso }}</td>
          <td>{{ formatarData(acesso.horaPermissao) }}</td>
          <td>{{ formatarData(acesso.horaExpiracao) }}</td>
          <td>{{ calcularTempoRestante(acesso.horaExpiracao) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { usefulTempoUtils } from '../utils/usefulTempoUtils'
import { useAuthStore } from '../store/auth'
import Navbar from '../components/Navbar.vue'

const store = useAuthStore()
const { formatarData, calcularTempoRestante, getTempoClasse } = usefulTempoUtils()

const acessos = ref([])

async function carregarAcessosUsuario() {
  try {
    acessos.value = await store.listarAcessosUsuario()
  } catch {
    alert('Erro ao carregar acessos do usuário.')
  }
}

onMounted(async () => {
  carregarAcessosUsuario()

  setInterval(() => {
    acessos.value = [...acessos.value]
  }, 60000)
})

</script>

<style scoped>
h3 {
  font-weight: 600;
}

.table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.modal-content {
  border-radius: 12px;
}

button {
  transition: all 0.2s ease;
}
</style>
