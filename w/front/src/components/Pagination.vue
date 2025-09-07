<!-- src/components/Pagination.vue -->
<script setup lang="ts">
const props = defineProps<{
  total: number
  size: number
  current: number
}>()
const emit = defineEmits<{ (e:'change', page:number): void }>()

function prev(){ if(props.current>1) emit('change', props.current-1) }
function next(){ const max = Math.ceil((props.total||0) / (props.size||10)); if(props.current<max) emit('change', props.current+1) }
</script>

<template>
  <div style="display:flex; align-items:center; gap:10px; margin-top:12px;">
    <span>共 {{ total }} 条</span>
    <button class="btn ghost" :disabled="current<=1" @click="prev">上一页</button>
    <span>第 {{ current }} / {{ Math.max(1, Math.ceil((total||0)/(size||10))) }} 页</span>
    <button class="btn ghost" :disabled="current >= Math.ceil((total||0)/(size||10))" @click="next">下一页</button>
  </div>
</template>
