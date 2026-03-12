import request from './config'

/**
 * 获取咨询历史
 */
export function getConsultationHistory(params) {
  return request({
    url: '/consultation/history',
    method: 'get',
    params
  })
}

/**
 * 创建新咨询
 */
export function createConsultation(data) {
  return request({
    url: '/consultation',
    method: 'post',
    data
  })
}

/**
 * 发送消息
 */
export function sendMessage(consultationId, data) {
  return request({
    url: `/consultation/${consultationId}/message`,
    method: 'post',
    data
  })
}

/**
 * 获取咨询详情
 */
export function getConsultationDetail(id) {
  return request({
    url: `/consultation/${id}`,
    method: 'get'
  })
}

/**
 * 获取咨询消息列表
 */
export function getConsultationMessages(id, params) {
  return request({
    url: `/consultation/${id}/messages`,
    method: 'get',
    params
  })
}

/**
 * 结束咨询
 */
export function endConsultation(id) {
  return request({
    url: `/consultation/${id}/end`,
    method: 'post'
  })
}

/**
 * 评价咨询
 */
export function rateConsultation(id, data) {
  return request({
    url: `/consultation/${id}/rate`,
    method: 'post',
    data
  })
}

/**
 * 删除咨询
 */
export function deleteConsultation(id) {
  return request({
    url: `/consultation/${id}`,
    method: 'delete'
  })
}