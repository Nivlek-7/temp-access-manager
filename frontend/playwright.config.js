import { defineConfig } from '@playwright/test'
import { loadEnvFile } from 'node:process'

try {
  loadEnvFile('../.env')
} catch {
  // As variáveis também podem ser informadas diretamente no ambiente.
}

export default defineConfig({
  testDir: './e2e',
  workers: 1,
  use: {
    baseURL: process.env.PLAYWRIGHT_BASE_URL || 'http://localhost:5173',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure'
  }
})
