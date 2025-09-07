import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios'
import router from '../router'
import { refreshWithRaw } from './auth/login'
import { isReactive, isRef, toRaw } from 'vue'

const baseURL = (import.meta as any).env?.VITE_API_URL || (typeof window !== 'undefined' && window.location?.hostname === 'localhost' ? 'http://localhost:7080' : window.location.origin)

const axiosInstance = axios.create({ baseURL })

const processReactive = (data: any) => {
  if (!data) return data
  let rawData = data
  if (isRef(rawData)) rawData = rawData.value
  if (isReactive(rawData)) rawData = toRaw(rawData)
  return rawData
}

// ===== Request interceptor: attach Authorization =====
axiosInstance.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  // attach token
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers = config.headers || {}
    ;(config.headers as any)['Authorization'] = `Bearer ${token}`
  }
  // unbox reactive payloads
  if (config.data) config.data = processReactive(config.data)
  if (config.params) config.params = processReactive(config.params)
  return config
})

// ===== Response interceptor: 401 -> refresh once -> replay =====
let isRefreshing = false
let queue: Array<(token: string | null) => void> = []

axiosInstance.interceptors.response.use(
  (resp) => resp,
  async (error: AxiosError) => {
    const response = error.response
    const original = error.config as any
    if (!response) return Promise.reject(error)

    // Only handle 401 once per request
    if (response.status === 401 && !original?.__isRetryRequest) {
      if (isRefreshing) {
        return new Promise((resolve) => {
          queue.push((newToken) => {
            if (newToken) {
              original.headers = original.headers || {}
              original.headers['Authorization'] = `Bearer ${newToken}`
            }
            original.__isRetryRequest = true
            resolve(axiosInstance(original))
          })
        })
      }

      isRefreshing = true
      try {
        const refreshToken = localStorage.getItem('refreshToken')
        if (!refreshToken) throw new Error('NO_REFRESH_TOKEN')

        const { data } = await refreshWithRaw(refreshToken)
        const body: any = data?.data ?? data
        const access = body?.accessToken || body?.token
        const refresh = body?.refreshToken || null

        if (!access) throw new Error('NO_ACCESS_FROM_REFRESH')

        localStorage.setItem('accessToken', access)
        if (refresh) localStorage.setItem('refreshToken', refresh)

        // drain queue
        queue.forEach(fn => fn(access))
        queue = []

        // replay current request
        original.headers = original.headers || {}
        original.headers['Authorization'] = `Bearer ${access}`
        original.__isRetryRequest = true
        return axiosInstance(original)
      } catch (e) {
        // fail: cleanup and route to login
        queue.forEach(fn => fn(null))
        queue = []
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        if (router.currentRoute.value.path !== '/login') {
          router.replace('/login')
        }
        return Promise.reject(e)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  }
)

// helper HTTP
export const get = (url: string, params?: any) => axiosInstance.get(url, { params })
export const post = (url: string, data?: any, config?: any) => axiosInstance.post(url, data, config)
export const put = (url: string, data?: any, config?: any) => axiosInstance.put(url, data, config)
export const deleteRequest = (url: string, config?: any) => axiosInstance.delete(url, config)
export const patch = (url: string, data?: any, config?: any) => axiosInstance.patch(url, data, config)

export default axiosInstance
