import request from './config'

/**
 * 获取通知列表
 */
export function getNotifications(params) {
  return request({
    url: '/notification/list',
    method: 'get',
    params
  })
}

/**
 * 标记为已读
 */
export function markAsRead(id) {
  return request({
    url: `/notification/${id}/read`,
    method: 'put'
  })
}

/**
 * 全部标记为已读
 */
export function markAllAsRead() {
  return request({
    url: '/notification/read-all',
    method: 'put'
  })
}

/**
 * 删除通知
 */
export function deleteNotification(id) {
  return request({
    url: `/notification/${id}`,
    method: 'delete'
  })
}

/**
 * 获取未读数量
 */
export function getUnreadCount() {
  return request({
    url: '/notification/unread-count',
    method: 'get'
  })
}