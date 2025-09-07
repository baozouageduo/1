import axios from 'axios'
import { post } from '../axiosRequest'

export interface LoginDTO {
  username: string
  password: string
  graphCaptchaKey?: string
  graphCaptchaCode?: string
  captchaKey?: string
  captchaCode?: string
  code?: string
}

// 正常登录（走带拦截器的实例即可）
export const login = (data: LoginDTO) => {
  const payload: any = {
    username: data.username,
    password: data.password
  }
  payload.graphCaptchaKey = data.graphCaptchaKey ?? data.captchaKey
  payload.captchaKey = payload.graphCaptchaKey
  payload.graphCaptchaCode = data.graphCaptchaCode ?? data.captchaCode ?? data.code
  payload.captchaCode = payload.graphCaptchaCode
  payload.code = payload.graphCaptchaCode
  return post('/auth/login', payload)
}

// 使用“裸 axios”刷新，避免拦截器递归 & 兼容 code=0/200
export const refreshWithRaw = (refreshToken: string) => {
  return axios.post('/auth/refreshToken', { refreshToken }, { baseURL: (import.meta as any).env?.VITE_API_URL || 'http://localhost:7080' })
}
