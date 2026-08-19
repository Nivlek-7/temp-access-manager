import { afterEach, describe, expect, it, vi } from 'vitest'
import { usefulTempoUtils } from './usefulTempoUtils'

describe('calcularTempoRestante', () => {
  afterEach(() => vi.useRealTimers())

  it('calcula dias, horas e minutos restantes', () => {
    vi.useFakeTimers()
    vi.setSystemTime(new Date('2026-08-18T12:00:00Z'))

    const { calcularTempoRestante } = usefulTempoUtils()

    expect(calcularTempoRestante('2026-08-19T14:03:00Z')).toBe('1 dia 2h 3min')
    expect(calcularTempoRestante('2026-08-18T11:59:59Z')).toBe('Expirado')
  })
})
