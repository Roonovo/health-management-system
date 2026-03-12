import request from './config'

/**
 * 获取用户列表
 */
export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 */
export function getUserDetail(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'get'
  })
}

/**
 * 更新用户信息
 */
export function updateUser(id, data) {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

/**
 * 禁用/启用用户
 */
export function toggleUserStatus(id, data) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data
  })
}

/**
 * 获取系统统计
 */
export function getSystemStats() {
  return request({
    url: '/admin/stats',
    method: 'get'
  })
}

/**
 * 获取系统日志
 */
export function getSystemLogs(params) {
  return request({
    url: '/admin/logs',
    method: 'get',
    params
  })
}

/**
 * 获取系统设置
 */
export function getSystemSettings() {
  return request({
    url: '/admin/settings',
    method: 'get'
  })
}

/**
 * 更新系统设置
 */
export function updateSystemSettings(data) {
  return request({
    url: '/admin/settings',
    method: 'put',
    data
  })
}