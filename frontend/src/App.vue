<template>
  <div :data-bs-theme="themeStore.theme" class="min-vh-100">
    <Navbar v-if="showNavbar" />
    <router-view />
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
import { useThemeStore } from './store/theme'

const themeStore = useThemeStore()

</script>

