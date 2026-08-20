import { expect, test } from '@playwright/test'

test('cadastro, aprovação, login, concessão e revogação', async ({ browser }) => {
  const adminEmail = process.env.E2E_ADMIN_EMAIL || process.env.ADMIN_EMAIL
  const adminPassword = process.env.E2E_ADMIN_PASSWORD || process.env.ADMIN_PASSWORD
  expect(adminEmail, 'Defina ADMIN_EMAIL ou E2E_ADMIN_EMAIL').toBeTruthy()
  expect(adminPassword, 'Defina ADMIN_PASSWORD ou E2E_ADMIN_PASSWORD').toBeTruthy()

  const id = Date.now()
  const nome = `Usuário E2E ${id}`
  const email = `e2e-${id}@example.com`
  const senha = 'Senha-e2e-123'
  const recurso = `Recurso E2E ${id}`

  const usuario = await browser.newContext()
  const paginaUsuario = await usuario.newPage()
  paginaUsuario.on('dialog', dialog => dialog.accept())

  await paginaUsuario.goto('/register')
  await paginaUsuario.getByLabel('Nome').fill(nome)
  await paginaUsuario.getByLabel('E-mail').fill(email)
  await paginaUsuario.getByLabel('Senha').fill(senha)
  const respostaCadastro = paginaUsuario.waitForResponse(response => response.url().endsWith('/api/auth/registrar'))
  await paginaUsuario.getByRole('button', { name: 'Registrar' }).click()
  const cadastro = await respostaCadastro
  expect(cadastro.ok(), `${cadastro.status()} ${await cadastro.text()}`).toBeTruthy()
  await expect(paginaUsuario).toHaveURL(/\/login$/)

  const administrador = await browser.newContext()
  const paginaAdmin = await administrador.newPage()
  paginaAdmin.on('dialog', dialog => dialog.accept())

  await paginaAdmin.goto('/login')
  await paginaAdmin.getByLabel('E-mail').fill(adminEmail)
  await paginaAdmin.getByLabel('Senha').fill(adminPassword)
  await paginaAdmin.getByRole('button', { name: 'Entrar' }).click()
  await expect(paginaAdmin).toHaveURL(/\/dashboard$/)

  const usuarioPendente = paginaAdmin.locator('tbody tr').filter({ hasText: email })
  await usuarioPendente.getByRole('button', { name: 'Aprovar' }).click()
  await expect(usuarioPendente).toHaveCount(0)

  await paginaUsuario.getByLabel('E-mail').fill(email)
  await paginaUsuario.getByLabel('Senha').fill(senha)
  await paginaUsuario.getByRole('button', { name: 'Entrar' }).click()
  await expect(paginaUsuario).toHaveURL(/\/home$/)

  await paginaAdmin.goto('/access')
  await paginaAdmin.getByRole('button', { name: 'Criar Novo Acesso' }).click()
  await paginaAdmin.locator('select.form-select').selectOption({ label: `${nome} (${email})` })
  await paginaAdmin.getByPlaceholder('Ex: Documento').fill(recurso)
  await paginaAdmin.getByPlaceholder('Minutos').fill('5')
  await paginaAdmin.getByRole('button', { name: 'Salvar Acesso' }).click()
  const acessoAdmin = paginaAdmin.locator('tbody tr').filter({ hasText: recurso }).first()
  await expect(acessoAdmin).toBeVisible()

  await paginaUsuario.reload()
  await expect(paginaUsuario.getByRole('cell', { name: recurso })).toBeVisible()

  await acessoAdmin.getByRole('button', { name: 'Revogar' }).click()
  await expect(paginaAdmin.locator('h3', { hasText: 'Acessos expirados' })
    .locator('..').getByRole('cell', { name: recurso })).toBeVisible()

  await paginaUsuario.reload()
  await expect(paginaUsuario.getByRole('cell', { name: recurso })).toHaveCount(0)

  await usuario.close()
  await administrador.close()
})
