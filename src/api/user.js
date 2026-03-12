import request from './config'

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request({
    url: '/user/profile',
    method: 'get'
  })
}

/**
 * 更新用户信息
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

/**
 * 上传头像
 */
export function uploadAvatar(formData) {
  return request({
    url: '/user/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取用户设置
 */
export function getUserSettings() {
  return request({
    url: '/user/settings',
    method: 'get'
  })
}

/**
 * 更新用户设置
 */
export function updateUserSettings(data) {
  return request({
    url: '/user/settings',
    method: 'put',
    data
  })
}

/**
 * 获取安全设置
 */
export function getSecuritySettings() {
  return request({
    url: '/user/security',
    method: 'get'
  })
}

/**
 * 更新安全设置
 */
export function updateSecuritySettings(data) {
  return request({
    url: '/user/security',
    method: 'put',
    data
  })
}

/**
 * 获取登录设备列表
 */
export function getLoginDevices() {
  return request({
    url: '/user/devices',
    method: 'get'
  })
}

/**
 * 移除登录设备
 */
export function removeDevice(deviceId) {
  return request({
    url: `/user/devices/${deviceId}`,
    method: 'delete'
  })
}

/**
 * 绑定邮箱
 */
export function bindEmail(data) {
  return request({
    url: '/user/email/bind',
    method: 'post',
    data
  })
}

/**
 * 修改邮箱
 */
export function changeEmail(data) {
  return request({
    url: '/user/email/change',
    method: 'post',
    data
  })
}

/**
 * 导出数据
 */
export function exportUserData() {
  return request({
    url: '/user/export',
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 注销账号
 */
export function deleteAccount(data) {
  return request({
    url: '/user/account',
    method: 'delete',
    data
  })
}

/**
 * 获取OCR识别结果
 */
export function getOcrResult(params) {
  return request({
    url: '/user/ocr',
    method: 'get',
    params
  })
}