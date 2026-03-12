package com.health.service.health;

import com.health.dto.response.AIResponse;
import com.health.entity.Conversation;
import com.health.entity.Message;

import java.util.List;

public interface HealthConsultService {

    /**
     * 发送健康咨询消息
     */
    AIResponse chat(String content, Long userId);

    /**
     * 创建新会话
     */
    Conversation createConversation(Long userId, String title);

    /**
     * 获取对话历史
     */
    List<Message> getConversationHistory(Long conversationId);

    /**
     * 获取用户的所有会话
     */
    List<Conversation> getUserConversations(Long userId);

    /**
     * 删除会话
     */
    boolean deleteConversation(Long conversationId);

    /**
     * 重新生成回复
     */
    AIResponse regenerateResponse(Long conversationId, Long messageId);
}