import request from './config'

/**
 * 获取健康数据列表
 */
export function getHealthDataList(params) {
  return request({
    url: '/health/data',
    method: 'get',
    params
  })
}

/**
 * 添加血压数据
 */
export function addBloodPressure(data) {
  return request({
    url: '/health/blood-pressure',
    method: 'post',
    data
  })
}

/**
 * 获取血压数据列表
 */
export function getBloodPressureList(params) {
  return request({
    url: '/health/blood-pressure',
    method: 'get',
    params
  })
}

/**
 * 获取血压趋势
 */
export function getBloodPressureTrend(params) {
  return request({
    url: '/health/blood-pressure/trend',
    method: 'get',
    params
  })
}

/**
 * 添加血糖数据
 */
export function addBloodSugar(data) {
  return request({
    url: '/health/blood-sugar',
    method: 'post',
    data
  })
}

/**
 * 获取血糖数据列表
 */
export function getBloodSugarList(params) {
  return request({
    url: '/health/blood-sugar',
    method: 'get',
    params
  })
}

/**
 * 获取血糖趋势
 */
export function getBloodSugarTrend(params) {
  return request({
    url: '/health/blood-sugar/trend',
    method: 'get',
    params
  })
}

/**
 * 添加体重数据
 */
export function addWeight(data) {
  return request({
    url: '/health/weight',
    method: 'post',
    data
  })
}

/**
 * 获取体重数据列表
 */
export function getWeightList(params) {
  return request({
    url: '/health/weight',
    method: 'get',
    params
  })
}

/**
 * 获取体重趋势
 */
export function getWeightTrend(params) {
  return request({
    url: '/health/weight/trend',
    method: 'get',
    params
  })
}

/**
 * 获取健康统计
 */
export function getHealthStats() {
  return request({
    url: '/health/stats',
    method: 'get'
  })
}

/**
 * 删除健康数据
 */
export function deleteHealthData(id) {
  return request({
    url: `/health/data/${id}`,
    method: 'delete'
  })
}

/**
 * 更新健康数据
 */
export function updateHealthData(id, data) {
  return request({
    url: `/health/data/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取健康记录详情
 */
export function getHealthRecordDetail(id) {
  return request({
    url: `/health/record/${id}`,
    method: 'get'
  })
}

/**
 * 创建健康记录
 */
export function createHealthRecord(data) {
  return request({
    url: '/health/record',
    method: 'post',
    data
  })
}

/**
 * 更新健康记录
 */
export function updateHealthRecord(id, data) {
  return request({
    url: `/health/record/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除健康记录
 */
export function deleteHealthRecord(id) {
  return request({
    url: `/health/record/${id}`,
    method: 'delete'
  })
}

/**
 * 获取健康报告
 */
export function getHealthReport(params) {
  return request({
    url: '/health/report',
    method: 'get',
    params
  })
}