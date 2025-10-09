<template>
  <Navbar />
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h3>Acessos ativos</h3>
      <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createAccessModal">
        <i class="bi bi-plus-circle me-1"></i> Criar Novo Acesso
      </button>
    </div>

    <div class="table-responsive mb-5">
      <table class="table table-bordered table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th>Ação</th>
            <th>Tipo de Recurso</th>
            <th>Nome do Usuário</th>
            <th>Tempo Restante</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="acesso in acessosAtivos" :key="acesso.id">
            <td>
              <button class="btn btn-outline-danger btn-sm" @click="revogarAcesso(acesso.id)">
                <i class="bi bi-x-circle me-1"></i> Revogar
              </button>
            </td>
            <td>{{ acesso.nomeRecurso }}</td>
            <td>{{ acesso.nomeUsuario }}</td>
            <td :class="getTempoClasse(acesso)">
              {{ calcularTempoRestante(acesso.horaExpiracao) }}
            </td>
          </tr>
          <tr v-if="acessosAtivos.length === 0">
            <td colspan="4" class="text-center text-muted">Nenhum acesso ativo.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <h3>Acessos expirados</h3>
    <div class="table-responsive">
      <table class="table table-bordered table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th>Tipo de Recurso</th>
            <th>Nome do Usuário</th>
            <th>Data Inicial</th>
            <th>Data que Expirou</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="acesso in acessosExpirados" :key="acesso.id">
            <td>{{ acesso.nomeRecurso }}</td>
            <td>{{ acesso.nomeUsuario }}</td>
            <td>{{ formatarData(acesso.horaPermissao) }}</td>
            <td>{{ formatarData(acesso.horaExpiracao) }}</td>
          </tr>
          <tr v-if="acessosExpirados.length === 0">
            <td colspan="4" class="text-center text-muted">Nenhum acesso expirado.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div
      class="modal fade"
      id="createAccessModal"
      tabindex="-1"
      aria-labelledby="createAccessModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog">
        <div class="modal-content bg-body-secondary text-body p-3">
          <div class="modal-header">
            <h5 class="modal-title" id="createAccessModalLabel">Criar Novo Acesso</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="permitirAcesso">
              <div class="mb-3">
                <label class="form-label">Usuário</label>
                <select v-model="usuarioId" class="form-select" required>
                  <option disabled value="">Selecione um usuário</option>
                  <option v-for="user in usuarios" :key="user.id" :value="user.id">
                    {{ user.nome }} ({{ user.email }})
                  </option>
                </select>
              </div>

              <div class="mb-3">
                <label class="form-label">Nome do Recurso</label>
                <input v-model="nomeRecurso" type="text" class="form-control" placeholder="Ex: Documento" required />
              </div>

              <div class="mb-3">
                <label class="form-label">Duração (Dias, horas, minutos)</label>
                <div class="d-flex gap-2">
                  <input v-model.number="dias" type="number" min="0" class="form-control" placeholder="Dias" />
                  <input v-model.number="horas" type="number" min="0" class="form-control" placeholder="Horas" />
                  <input v-model.number="minutos" type="number" min="0" class="form-control" placeholder="Minutos" />
                </div>
              </div>

              <button type="submit" class="btn btn-success w-100">Salvar Acesso</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../store/auth'
import { usefulTempoUtils } from '../utils/usefulTempoUtils'
import Navbar from '../components/Navbar.vue'
const store = useAuthStore()

const acessosAtivos = ref([])
const acessosExpirados = ref([])
const usuarios = ref([])

const usuarioId = ref('')
const nomeRecurso = ref('')
const dias = ref(0)
const horas = ref(0)
const minutos = ref(0)

const { formatarData, calcularTempoRestante, getTempoClasse } = usefulTempoUtils()

async function carregarDados() {
  try {
    const [acessosResp, usuariosResp] = await Promise.all([
      store.listarAcessos(),
      store.listarUsuarios()
    ])

    // Ordena acessos ativos pelo tempo restante (menor para o maior)
    acessosAtivos.value = acessosResp
      .filter(a => !a.revogado)
      .sort((a, b) => {
        const restanteA = new Date(a.horaExpiracao) - new Date()
        const restanteB = new Date(b.horaExpiracao) - new Date()
        return restanteA - restanteB
      })

    // Ordena acessos expirados pela data de expiração (mais recente primeiro)
    acessosExpirados.value = acessosResp
      .filter(a => a.revogado)
      .sort((a, b) => new Date(b.horaExpiracao) - new Date(a.horaExpiracao))

    usuarios.value = usuariosResp
  } catch (error) {
    console.error('Erro ao carregar dados:', error)
  }
}

function converterParaSegundos() {
  return dias.value * 86400 + horas.value * 3600 + minutos.value * 60
}

async function permitirAcesso() {
  const duracaoSegundos = converterParaSegundos()
  try {
    await store.permitirAcesso(usuarioId.value, nomeRecurso.value, duracaoSegundos)
    alert('Acesso criado com sucesso!')
    carregarDados()
    limparCampos()
    bootstrap.Modal.getInstance(document.getElementById('createAccessModal')).hide()
  } catch (err) {
  }
}

async function revogarAcesso(id) {
  if (!confirm('Deseja realmente revogar este acesso?')) return
  try {
    await store.revogarAcesso(id)
    alert('Acesso revogado!')
    carregarDados()
  } catch {
    alert('Erro ao revogar acesso.')
  }
}

function limparCampos() {
  usuarioId.value = ''
  nomeRecurso.value = ''
  dias.value = 0
  horas.value = 0
  minutos.value = 0
}

onMounted(() => {
  carregarDados()

  setInterval(() => {
    acessosAtivos.value = [...acessosAtivos.value]
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
  font-size: 0.95rem;
}

.modal-content {
  border-radius: 12px;
}

button {
  transition: all 0.2s ease;
}
</style>