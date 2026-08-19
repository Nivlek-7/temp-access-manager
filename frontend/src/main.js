import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { useAuthStore } from './store/auth'
import router from './router'
import App from './App.vue'

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
app.use(router)
useAuthStore(pinia).scheduleLogout()
app.mount('#app')
