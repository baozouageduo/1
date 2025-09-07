<!-- src/components/BaseLayout.vue -->
<script setup lang="ts">
import Navbar from './Navbar.vue'
</script>

<template>
  <div>
    <div class="navbar">
      <div class="inner">
        <div style="display:flex;align-items:center;gap:10px;">
          <strong>Campus L&F</strong>
          <span class="badge">frontend</span>
        </div>
        <Navbar />
      </div>
    </div>
    <div class="container">
      <slot />
    </div>
    <div class="footer">
      Campus Lost & Found • UI optimized • Vue3 + TS
    </div>
  </div>
</template>
