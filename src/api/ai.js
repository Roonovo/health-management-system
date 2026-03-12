import request from '@/utils/request'

/**
 * 发送 AI 咨询消息
 * @param {Object} data - { message: string, sessionId?: string }
 */
export function sendAIMessage(data) {
  return request({
    url: '/api/ai/chat',
    method: 'post',
    data
  })
}

/**
 * 获取对话历史
 * @param {string} sessionId - 会话 ID（可选）
 */
export function getChatHistory(sessionId) {
  return request({
    url: '/api/ai/history',
    method: 'get',
    params: {
      sessionId
    }
  })
}

/**
 * 创建新的对话会话
 */
export function createChatSession() {
  return request({
    url: '/api/ai/session',
    method: 'post'
  })
}

/**
 * 获取所有对话会话列表
 */
export function getChatSessions() {
  return request({
    url: '/api/ai/sessions',
    method: 'get'
  })
}

/**
 * 删除对话会话
 * @param {string} sessionId - 会话 ID
 */
export function deleteChatSession(sessionId) {
  return request({
    url: `/api/ai/session/${sessionId}`,
    method: 'delete'
  })
}

/**
 * 获取快捷问题列表
 */
export function getQuickQuestions() {
  return request({
    url: '/api/ai/quick-questions',
    method: 'get'
  })
}

/**
 * 获取 AI 建议（基于用户健康数据）
 */
export function getAIRecommendations() {
  return request({
    url: '/api/ai/recommendations',
    method: 'get'
  })
}

/**
 * 评价 AI 回答
 * @param {Object} data - { messageId: string, rating: number, feedback?: string }
 */
export function rateAIResponse(data) {
  return request({
    url: '/api/ai/rate',
    method: 'post',
    data
  })
}

/**
 * 流式对话（SSE - Server-Sent Events）
 * @param {Object} data - { message: string, sessionId?: string }
 */
export function streamAIMessage(data) {
  return request({
    url: '/api/ai/stream',
    method: 'post',
    data,
    responseType: 'stream'
  })
}