<!-- src/components/Navbar.vue -->
<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()

function logout() {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  router.push('/login')
}
</script>

<template>
  <div class="navlinks">
    <RouterLink :class="{active: route.path.startsWith('/dashboard')}" to="/dashboard">仪表盘</RouterLink>
    <RouterLink :class="{active: route.path.startsWith('/items')}" to="/items">物品管理</RouterLink>
    <RouterLink :class="{active: route.path.startsWith('/lost')}" to="/lost">公告管理</RouterLink>
    <button class="btn secondary" @click="logout">退出</button>
  </div>
</template>
