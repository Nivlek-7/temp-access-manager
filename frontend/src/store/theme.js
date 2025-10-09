import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

// tentativa de aplicar o modo escuro antes de usar o Bootstrap 5.3
export const useThemeStore = defineStore('theme', () => {
  const theme = ref(localStorage.getItem('theme') || 'light')

  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
  }

  watch(theme, (newTheme) => {
    document.documentElement.setAttribute('data-bs-theme', newTheme)
    localStorage.setItem('theme', newTheme)
  }, { immediate: true })

  return { theme, toggleTheme }
})
