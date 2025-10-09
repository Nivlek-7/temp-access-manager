export function usefulTempoUtils() {

  function calcularTempoRestante(horaExpiracao) {
    const agora = new Date()
    const exp = new Date(horaExpiracao)
    let restanteMs = exp - agora
    if (restanteMs <= 0) return 'Expirado'

    const dias = Math.floor(restanteMs / (24 * 3600000))
    restanteMs %= 24 * 3600000

    const horas = Math.floor(restanteMs / 3600000)
    restanteMs %= 3600000

    const minutos = Math.floor(restanteMs / 60000)

    const partes = []
    if (dias > 0) partes.push(`${dias} dia${dias > 1 ? 's' : ''}`)
    if (horas > 0) partes.push(`${horas}h`)
    if (minutos > 0) partes.push(`${minutos}min`)

    return partes.join(' ') || 'menos de 1 min'
  }

  function getTempoClasse(acesso) {
    const agora = new Date()
    const exp = new Date(acesso.horaExpiracao)
    const restanteMs = exp - agora
    return restanteMs <= 3600000 ? 'text-danger fw-semibold' : ''
  }

  function formatarData(data) {
    const d = new Date(data)
    return d.toLocaleString('pt-BR')
  }

  return { formatarData, calcularTempoRestante, getTempoClasse }
}