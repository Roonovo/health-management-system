/**
 * 本地存储工具
 */

const STORAGE_PREFIX = 'health_'

/**
 * 设置本地存储
 */
export function setStorage(key, value) {
  try {
    const data = JSON.stringify(value)
    localStorage.setItem(STORAGE_PREFIX + key, data)
  } catch (error) {
    console.error('setStorage error:', error)
  }
}

/**
 * 获取本地存储
 */
export function getStorage(key) {
  try {
    const data = localStorage.getItem(STORAGE_PREFIX + key)
    return data ? JSON.parse(data) : null
  } catch (error) {
    console.error('getStorage error:', error)
    return null
  }
}

/**
 * 移除本地存储
 */
export function removeStorage(key) {
  try {
    localStorage.removeItem(STORAGE_PREFIX + key)
  } catch (error) {
    console.error('removeStorage error:', error)
  }
}

/**
 * 清空本地存储
 */
export function clearStorage() {
  try {
    const keys = Object.keys(localStorage)
    keys.forEach(key => {
      if (key.startsWith(STORAGE_PREFIX)) {
        localStorage.removeItem(key)
      }
    })
  } catch (error) {
    console.error('clearStorage error:', error)
  }
}

/**
 * Token 相关
 */
export function getToken() {
  return getStorage('token')
}

export function setToken(token) {
  setStorage('token', token)
}

export function removeToken() {
  removeStorage('token')
}

/**
 * 用户信息相关
 */
export function getUserInfo() {
  return getStorage('userInfo')
}

export function setUserInfo(userInfo) {
  setStorage('userInfo', userInfo)
}

export function removeUserInfo() {
  removeStorage('userInfo')
}