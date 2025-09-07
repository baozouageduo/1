<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getLostPages, createLost, updateLost, deleteLost, type LostNotice, type LostSelectionDTO } from '../api/lost'
import Pagination from '../components/Pagination.vue'

const rows = ref<LostNotice[]>([])
const page = reactive({ pageNumber: 1, pageSize: 10, total: 0 })
const filters = reactive<LostSelectionDTO>({ name: '', status: undefined })
const loading = ref(false)
const dialog = reactive({ visible:false, editingId: 0 })
const form = reactive<LostNotice>({ title:'', description:'', status:0, contact:'', placeId: undefined })

async function fetchData(){
  loading.value = true
  try{
    const { data } = await getLostPages({ filters, pageNumber: page.pageNumber, pageSize: page.pageSize })
    const r = data?.data || {}
    rows.value = r.records || []
    page.total = r.total || 0
    page.pageNumber = r.current || page.pageNumber
    page.pageSize = r.size || page.pageSize
  } finally{
    loading.value = false
  }
}
onMounted(fetchData)

function openCreate(){
  dialog.visible = true
  dialog.editingId = 0
  Object.assign(form, { title:'', description:'', status:0, contact:'', placeId: undefined })
}
function openEdit(row: LostNotice){
  dialog.visible = true
  dialog.editingId = row.id || 0
  Object.assign(form, row)
}
async function save(){
  if(dialog.editingId){
    await updateLost({ form, id: dialog.editingId })
  }else{
    await createLost({ form })
  }
  dialog.visible = false
  await fetchData()
}
async function remove(id?: number){
  if(!id) return
  if(confirm('确认删除该公告？')){
    await deleteLost(id)
    await fetchData()
  }
}
function onPageChange(p:number){ page.pageNumber = p; fetchData() }
</script>

<template>
  <div>
    <div class="header">
      <h2 style="margin:0">公告管理</h2>
      <div class="actions">
        <button class="btn" @click="openCreate">新增公告</button>
        <button class="btn secondary" @click="() => { page.pageNumber=1; fetchData() }">刷新</button>
      </div>
    </div>

    <div class="card grid cols-2" style="margin-bottom:14px">
      <div>
        <label>关键词</label>
        <input v-model="filters.name" class="input" placeholder="标题/描述" @keyup.enter="() => { page.pageNumber=1; fetchData() }"/>
      </div>
      <div>
        <label>状态</label>
        <select v-model="(filters.status as any)" class="select">
          <option :value="undefined">全部</option>
          <option :value="0">未处理</option>
          <option :value="1">已处理</option>
        </select>
      </div>
      <div>
        <button class="btn" @click="() => { page.pageNumber=1; fetchData() }">搜索</button>
      </div>
    </div>

    <div class="card">
      <div v-if="loading">加载中</div>
      <table v-else class="table">
        <thead>
          <tr>
            <th style="width:70px">ID</th>
            <th>标题</th>
            <th>描述</th>
            <th style="width:120px">状态</th>
            <th style="width:200px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in rows" :key="r.id">
            <td>{{ r.id }}</td>
            <td>{{ r.title }}</td>
            <td>{{ r.description }}</td>
            <td>
              <span class="badge" :class="r.status===1 ? 'status-1' : 'status-0'">
                {{ r.status===1 ? '已处理' : '未处理' }}
              </span>
            </td>
            <td>
              <div style="display:flex; gap:8px; flex-wrap:wrap">
                <button class="btn secondary" @click="openEdit(r)">编辑</button>
                <button class="btn ghost" @click="remove(r.id)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!rows.length">
            <td colspan="5" style="text-align:center; padding:24px; color:var(--muted)">暂无数据</td>
          </tr>
        </tbody>
      </table>
      <Pagination :total="page.total" :size="page.pageSize" :current="page.pageNumber" @change="onPageChange"/>
    </div>

    <div v-if="dialog.visible" class="modal" style="position:fixed; inset:0; background:rgba(0,0,0,.45); display:grid; place-items:center">
      <div class="card" style="width:620px; max-width:95vw">
        <h3 style="margin-top:0">{{ dialog.editingId ? '编辑公告' : '新增公告' }}</h3>
        <div class="grid cols-2">
          <div class="col-span-2">
            <label>标题</label>
            <input v-model="form.title" class="input"/>
          </div>
          <div class="col-span-2">
            <label>描述</label>
            <textarea v-model="form.description" rows="4" class="textarea"/>
          </div>
          <div>
            <label>联系方式</label>
            <input v-model="form.contact" class="input"/>
          </div>
        </div>
        <div style="display:flex; justify-content:flex-end; gap:8px; margin-top:16px">
          <button class="btn ghost" @click="dialog.visible=false">取消</button>
          <button class="btn" @click="save">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>
