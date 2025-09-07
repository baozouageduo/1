<script setup lang="ts">
import { ref, onMounted } from 'vue'
import router from '../router'
import { login, type LoginDTO } from '../api/auth/login'
import axios from 'axios'
import { parseJwt, deriveRole, routeForRole } from '../utils/auth'

const loading = ref(false)
const form = ref<LoginDTO>({
  username: '',
  password: '',
  graphCaptchaKey: '',
  graphCaptchaCode: ''
})

const captchaImg = ref<string>('')
const baseURL = (import.meta as any).env?.VITE_API_URL || 'http://localhost:7080'

function sanitizeDataUrl(dataUrl: string) {
  if (typeof dataUrl !== 'string') return ''
  const s = dataUrl.trim()
  if (!s.startsWith('data:image/')) return s
  const idx = s.indexOf(',')
  if (idx < 0) return s.replace(/\s+/g, '')
  const prefix = s.slice(0, idx + 1)
  const body = s.slice(idx + 1).replace(/\s+/g, '')
  return prefix + body
}

function dataUrlToObjectUrl(dataUrl: string): string {
  const s = sanitizeDataUrl(dataUrl)
  if (!s.startsWith('data:image/')) return ''
  const idx = s.indexOf(',')
  const header = s.slice(0, idx)
  const body   = s.slice(idx + 1)
  const mime   = header.split(':')[1]?.split(';')[0] || 'image/png'
  try {
    const binStr = atob(body)
    const bytes = new Uint8Array(binStr.length)
    for (let i=0;i<binStr.length;i++) bytes[i] = binStr.charCodeAt(i)
    const blob = new Blob([bytes], { type: mime === 'image/jpg' ? 'image/jpeg' : mime })
    return URL.createObjectURL(blob)
  } catch (e) {
    console.error('[Captcha] dataURL decode failed:', e)
    return s // fallback to raw dataURL
  }
}

function onImgError() {
  console.warn('[Captcha] <img> error, placeholder fallback')
  captchaImg.value =
    'data:image/svg+xml;base64,' +
    btoa(`<svg xmlns="http://www.w3.org/2000/svg" width="120" height="40"><rect width="100%" height="100%" fill="#eee"/><text x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" fill="#999" font-size="12">Captcha</text></svg>`)
}

async function getCaptcha() {
  try {
    const resp = await axios.get('/auth/GraphCaptcha', {
      baseURL,
      params: { t: Date.now() },
      withCredentials: false,
      responseType: 'json'
    })
    const root = resp?.data ?? {}
    const data = root?.data ?? {}

    // YOUR backend fields
    const key = data?.captchaKey || ''
    const img = data?.captchaImage || '' // data:image/jpg;base64,...

    if (key) {
      form.value.graphCaptchaKey = key
      ;(form.value as any).captchaKey = key
    } else {
      form.value.graphCaptchaKey = ''
      ;(form.value as any).captchaKey = ''
    }

    if (img) {
      // Prefer object URL for robustness
      const src = img.startsWith('data:image/') ? dataUrlToObjectUrl(img) : img
      captchaImg.value = src
    } else {
      onImgError()
    }
  } catch (e) {
    console.error('[Captcha] fetch failed:', e)
    onImgError()
  }
}

async function doLogin() {
  if (!form.value.username || !form.value.password) return
  if (!form.value.graphCaptchaCode) return
  loading.value = true
  try {
    const { data } = await login(form.value)
    const payload = (data && (data.data ?? data)) || data
    const accessToken =
      payload?.accessToken ?? payload?.token ?? payload?.access_token
    const refreshToken =
      payload?.refreshToken ?? payload?.refresh_token ?? payload?.rt
    if (!accessToken) throw new Error('登录成功但未返回 accessToken')

    localStorage.setItem('accessToken', accessToken)
    if (refreshToken) localStorage.setItem('refreshToken', refreshToken)
    const jwt = parseJwt(accessToken)
    const role = deriveRole(jwt, null)
    const target = routeForRole(role)
    router.replace(target)
  } catch (err) {
    console.error(err)
    await getCaptcha()
  } finally {
    loading.value = false
  }
}

function onEnter(e: KeyboardEvent) {
  if (e.key === 'Enter') doLogin()
}

onMounted(() => {
  getCaptcha()
})
</script>

<template>
  <div class="login-page">
    <div class="card">
      <h1 class="title">校园失物招领系统</h1>

      <div class="form" role="form" aria-label="登录表单">
        <label class="label" for="login-username">账号</label>
        <input
          id="login-username"
          name="username"
          v-model="form.username"
          class="input"
          placeholder="请输入账号"
          autocomplete="username"
          @keydown="onEnter"
        />

        <label class="label" for="login-password">密码</label>
        <input
          id="login-password"
          name="password"
          v-model="form.password"
          class="input"
          placeholder="请输入密码"
          type="password"
          autocomplete="current-password"
          @keydown="onEnter"
        />

        <label class="label" for="login-captcha">验证码</label>
        <div class="captcha-row">
          <input
            id="login-captcha"
            name="captcha"
            v-model="form.graphCaptchaCode"
            class="input captcha-input"
            placeholder="输入验证码"
            maxlength="8"
            @keydown="onEnter"
          />
          <img
            class="captcha-img"
            :src="captchaImg"
            alt="验证码"
            title="点击刷新"
            @click="getCaptcha"
            @error="onImgError"
            crossorigin="anonymous"
          />
        </div>

        <button class="btn" :disabled="loading" @click="doLogin">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: #f6f7fb;
}

.card {
  width: 360px;
  max-width: 92vw;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
  padding: 28px 24px 24px 24px;
}

.title {
  margin: 0 0 14px 0;
  font-size: 20px;
  font-weight: 700;
  text-align: center;
}

.form {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.label {
  font-size: 13px;
  color: #555;
  margin-top: 8px;
}

.input {
  height: 38px;
  border: 1px solid #d9d9d9;
  border-radius: 10px;
  padding: 0 12px;
  outline: none;
  transition: border-color 0.15s ease;
  font-size: 14px;
  width: 100%;
  background: #fff;
}
.input:focus { border-color: #409eff; }

.captcha-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-input { width: 180px; }

.captcha-img {
  height: 40px;
  display: inline-block;
  user-select: none;
  -webkit-user-select: none;
  cursor: pointer;
  border-radius: 8px;
  border: 1px solid #eee;
}

.btn {
  margin-top: 14px;
  height: 40px;
  border: none;
  background: #409eff;
  color: #fff;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.15s ease;
}
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
</style>