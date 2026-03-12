import request from './config'

/**
 * 获取健康计划列表
 */
export function getHealthPlans(params) {
  return request({
    url: '/plan/list',
    method: 'get',
    params
  })
}

/**
 * 获取计划详情
 */
export function getHealthPlanDetail(id) {
  return request({
    url: `/plan/${id}`,
    method: 'get'
  })
}

/**
 * 创建健康计划
 */
export function createHealthPlan(data) {
  return request({
    url: '/plan',
    method: 'post',
    data
  })
}

/**
 * 更新健康计划
 */
export function updateHealthPlan(id, data) {
  return request({
    url: `/plan/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除健康计划
 */
export function deleteHealthPlan(id) {
  return request({
    url: `/plan/${id}`,
    method: 'delete'
  })
}

/**
 * 计划打卡
 */
export function checkInPlan(id, data) {
  return request({
    url: `/plan/${id}/checkin`,
    method: 'post',
    data
  })
}

/**
 * 获取打卡记录
 */
export function getCheckInRecords(id, params) {
  return request({
    url: `/plan/${id}/checkin-records`,
    method: 'get',
    params
  })
}

/**
 * 获取计划进度
 */
export function getPlanProgress(id) {
  return request({
    url: `/plan/${id}/progress`,
    method: 'get'
  })
}

/**
 * 获取计划统计
 */
export function getPlanStatistics(params) {
  return request({
    url: '/plan/statistics',
    method: 'get',
    params
  })
}