import { login, logout, getCurrentUser } from '@/api/auth'
import { getUserProfile } from '@/api/user'
import { setToken, getToken, removeToken, setUserInfo, getUserInfo, removeUserInfo } from '@/utils/storage'

const state = {
  token: getToken(),
  userInfo: getUserInfo(),
  profile: null
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    setToken(token)
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    setUserInfo(userInfo)
  },
  SET_PROFILE(state, profile) {
    state.profile = profile
  },
  CLEAR_USER(state) {
    state.token = ''
    state.userInfo = null
    state.profile = null
    removeToken()
    removeUserInfo()
  }
}

const actions = {
  // 登录
  async login({ commit }, loginForm) {
    try {
      const res = await login(loginForm)
      if (res.success && res.data.token) {
        commit('SET_TOKEN', res.data.token)
        commit('SET_USER_INFO', res.data.userInfo)
        return res
      }
      return Promise.reject(new Error('登录失败'))
    } catch (error) {
      return Promise.reject(error)
    }
  },
  
  // 获取用户信息
  async getUserInfo({ commit }) {
    try {
      const res = await getCurrentUser()
      if (res.success) {
        commit('SET_USER_INFO', res.data)
        return res.data
      }
      return Promise.reject(new Error('获取用户信息失败'))
    } catch (error) {
      return Promise.reject(error)
    }
  },
  
  // 获取用户资料
  async getUserProfile({ commit }) {
    try {
      const res = await getUserProfile()
      if (res.success) {
        commit('SET_PROFILE', res.data)
        return res.data
      }
      return Promise.reject(new Error('获取用户资料失败'))
    } catch (error) {
      return Promise.reject(error)
    }
  },
  
  // 退出登录
  async logout({ commit }) {
    try {
      await logout()
      commit('CLEAR_USER')
    } catch (error) {
      commit('CLEAR_USER')
      return Promise.reject(error)
    }
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}