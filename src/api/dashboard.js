import request from './config'

/**
 * 获取仪表盘数据
 */
export function getDashboardData() {
  return request({
    url: '/dashboard',
    method: 'get'
  })
}

/**
 * 获取统计数据
 */
export function getStatistics(params) {
  return request({
    url: '/dashboard/statistics',
    method: 'get',
    params
  })
}

/**
 * 获取今日概览
 */
export function getTodayOverview() {
  return request({
    url: '/dashboard/today',
    method: 'get'
  })
}

/**
 * 获取健康趋势
 */
export function getHealthTrend(params) {
  return request({
    url: '/dashboard/trend',
    method: 'get',
    params
  })
}

/**
 * 获取待办事项
 */
export function getTodoList() {
  return request({
    url: '/dashboard/todos',
    method: 'get'
  })
}