import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '@/router'
import store from '@/store'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || 'http://localhost:8080',
  timeout: 30000,
  withCredentials: true
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从 store 或 localStorage 获取 token
    const token = store.getters.token || localStorage.getItem('token')
    
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 开发环境打印请求信息
    if (import.meta.env.DEV) {
      console.log('🚀 Request:', {
        method: config.method.toUpperCase(),
        url: config.url,
        params: config.params,
        data: config.data
      })
    }
    
    return config
  },
  error => {
    console.error('❌ Request Error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 开发环境打印响应信息
    if (import.meta.env.DEV) {
      console.log('✅ Response:', {
        url: response.config.url,
        data: res
      })
    }
    
    // 后端统一返回格式: { success: boolean, data: any, message: string, code: number }
    if (res.success === false) {
      // 显示错误消息
      ElMessage.error(res.message || '请求失败')
      
      // 特殊错误码处理
      if (res.code === 401) {
        // token 过期或未授权
        handleUnauthorized()
      } else if (res.code === 403) {
        ElMessage.error('没有权限访问')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    // 返回成功数据
    return res
  },
  error => {
    console.error('❌ Response Error:', error)
    
    let message = '网络错误'
    
    if (error.response) {
      const status = error.response.status
      
      switch (status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请登录'
          handleUnauthorized()
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务不可用'
          break
        default:
          message = error.response.data?.message || `服务器错误 (${status})`
      }
    } else if (error.request) {
      message = '网络连接失败，请检查网络'
    } else {
      message = error.message || '请求失败'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 处理未授权
function handleUnauthorized() {
  ElMessageBox.confirm(
    '登录状态已过期，请重新登录',
    '系统提示',
    {
      confirmButtonText: '重新登录',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 清除 token
    store.dispatch('user/logout')
    localStorage.removeItem('token')
    
    // 跳转到登录页
    router.push({
      path: '/login',
      query: { redirect: router.currentRoute.value.fullPath }
    })
  }).catch(() => {
    // 取消
  })
}

export default service