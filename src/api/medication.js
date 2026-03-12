import request from './config'

/**
 * 获取用药计划列表
 */
export function getMedicationPlans(params) {
  return request({
    url: '/medication/plans',
    method: 'get',
    params
  })
}

/**
 * 获取用药计划详情
 */
export function getMedicationPlanDetail(id) {
  return request({
    url: `/medication/plans/${id}`,
    method: 'get'
  })
}

/**
 * 创建用药计划
 */
export function createMedicationPlan(data) {
  return request({
    url: '/medication/plans',
    method: 'post',
    data
  })
}

/**
 * 更新用药计划
 */
export function updateMedicationPlan(id, data) {
  return request({
    url: `/medication/plans/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用药计划
 */
export function deleteMedicationPlan(id) {
  return request({
    url: `/medication/plans/${id}`,
    method: 'delete'
  })
}

/**
 * 记录服药
 */
export function recordMedication(data) {
  return request({
    url: '/medication/records',
    method: 'post',
    data
  })
}

/**
 * 获取服药记录
 */
export function getMedicationRecords(params) {
  return request({
    url: '/medication/records',
    method: 'get',
    params
  })
}

/**
 * 获取用药日历
 */
export function getMedicationCalendar(params) {
  return request({
    url: '/medication/calendar',
    method: 'get',
    params
  })
}

/**
 * 获取用药提醒设置
 */
export function getReminderSettings() {
  return request({
    url: '/medication/reminder',
    method: 'get'
  })
}

/**
 * 更新用药提醒设置
 */
export function updateReminderSettings(data) {
  return request({
    url: '/medication/reminder',
    method: 'put',
    data
  })
}

/**
 * 获取用药统计
 */
export function getMedicationStats(params) {
  return request({
    url: '/medication/stats',
    method: 'get',
    params
  })
}

/**
 * 批量更新用药计划
 */
export function batchUpdatePlans(data) {
  return request({
    url: '/medication/plans/batch',
    method: 'put',
    data
  })
}

/**
 * 批量删除用药计划
 */
export function batchDeletePlans(ids) {
  return request({
    url: '/medication/plans/batch',
    method: 'delete',
    data: { ids }
  })
}