import request from '@/utils/request';

// 获取健康目标
export function getHealthGoals() {
  return request({
    url: '/api/health-plan/goals',
    method: 'get'
  });
}

// 更新健康目标
export function updateHealthGoals(data) {
  return request({
    url: '/api/health-plan/goals',
    method: 'put',
    data
  });
}

// 获取固定日程
export function getFixedEvents(params) {
  return request({
    url: '/api/health-plan/fixed-events',
    method: 'get',
    params
  });
}

// 添加固定日程
export function addFixedEvent(data) {
  return request({
    url: '/api/health-plan/fixed-events',
    method: 'post',
    data
  });
}

// 更新固定日程
export function updateFixedEvent(id, data) {
  return request({
    url: `/api/health-plan/fixed-events/${id}`,
    method: 'put',
    data
  });
}

// 删除固定日程
export function deleteFixedEvent(id) {
  return request({
    url: `/api/health-plan/fixed-events/${id}`,
    method: 'delete'
  });
}

// 生成智能日程
export function generateSmartSchedule(data) {
  return request({
    url: '/api/health-plan/generate-schedule',
    method: 'post',
    data
  });
}

// 获取健康活动
export function getHealthActivities(params) {
  return request({
    url: '/api/health-plan/health-activities',
    method: 'get',
    params
  });
}

// 更新健康活动
export function updateHealthActivity(id, data) {
  return request({
    url: `/api/health-plan/health-activities/${id}`,
    method: 'put',
    data
  });
}

// 删除健康活动
export function deleteHealthActivity(id) {
  return request({
    url: `/api/health-plan/health-activities/${id}`,
    method: 'delete'
  });
}

// 检测日程冲突
export function detectConflicts(data) {
  return request({
    url: '/api/health-plan/detect-conflicts',
    method: 'post',
    data
  });
}

// 获取优化建议
export function getOptimizationSuggestions(params) {
  return request({
    url: '/api/health-plan/suggestions',
    method: 'get',
    params
  });
}

// 获取健康统计
export function getHealthStatistics(params) {
  return request({
    url: '/api/health-plan/statistics',
    method: 'get',
    params
  });
}

// 标记活动完成
export function markActivityCompleted(id) {
  return request({
    url: `/api/health-plan/health-activities/${id}/complete`,
    method: 'post'
  });
}

// 导出日程
export function exportSchedule(params) {
  return request({
    url: '/api/health-plan/export',
    method: 'get',
    params,
    responseType: 'blob'
  });
}