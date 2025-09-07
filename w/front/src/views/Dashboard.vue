<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getItemTypeCount } from '../api/items'

const counts = ref<{ name: string; count: number }[]>([])

onMounted(async () => {
  try {
    const { data } = await getItemTypeCount()
    counts.value = data?.data || []
  } catch (e) {
    // ignore
  }
})
</script>

<template>
  <div>
    <div class="header">
      <h2 style="margin:0">仪表盘</h2>
      <div class="actions">
        <RouterLink class="btn secondary" to="/items">物品管理</RouterLink>
        <RouterLink class="btn secondary" to="/lost">公告管理</RouterLink>
      </div>
    </div>
    <div class="kpis">
      <div v-for="c in counts" :key="c.name" class="kpi">
        <div class="t">{{ c.name }}</div>
        <div class="v">{{ c.count }}</div>
      </div>
    </div>
    <div class="card">
      <div style="color:var(--muted)">系统总览：快速导航和关键指标展示。</div>
    </div>
  </div>
</template>
