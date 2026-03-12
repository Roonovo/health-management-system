package com.health.service.health.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.dto.request.AIRequestContext;
import com.health.dto.response.AIResponse;
import com.health.dto.response.ConversationMessage;
import com.health.dto.response.HealthIndicatorDTO;
import com.health.dto.response.HealthProfileContext;
import com.health.entity.Conversation;
import com.health.entity.Message;
import com.health.entity.User;
import com.health.mapper.ConversationMapper;
import com.health.mapper.HealthProfileMapper;
import com.health.mapper.MessageMapper;
import com.health.service.ai.AIService;
import com.health.service.health.HealthConsultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Slf4j
@Service
public class HealthConsultServiceImpl implements HealthConsultService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private AIService aiService;

    @Autowired
    private HealthProfileMapper healthProfileMapper;

    @Override
    @Transactional
    public AIResponse chat(String content, Long userId) {
        // 查找或创建会话
        Conversation conversation = getOrCreateConversation(userId);

        // 保存用户消息
        Message userMessage = new Message();
        userMessage.setConversationId(conversation.getId());
        userMessage.setContent(content);
        userMessage.setSenderType("USER");
        userMessage.setCreateTime(LocalDateTime.now());
        messageMapper.insert(userMessage);

        // 构建AI请求上下文
        AIRequestContext context = buildAIRequestContext(userId, conversation.getId());

        // 调用AI服务
        AIResponse aiResponse = aiService.chat(content, context);

        // 保存AI回复
        Message aiMessage = new Message();
        aiMessage.setConversationId(conversation.getId());
        aiMessage.setContent(aiResponse.getContent());
        aiMessage.setSenderType("AI");
        aiMessage.setCreateTime(LocalDateTime.now());
        messageMapper.insert(aiMessage);

        // 更新会话
        conversation.setLastMessage(aiResponse.getContent());
        conversation.setMessageCount(conversation.getMessageCount() + 2);
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.update(conversation);

        // 设置消息ID
        aiResponse.setMessageId(aiMessage.getId());

        return aiResponse;
    }

    @Override
    @Transactional
    public Conversation createConversation(Long userId, String title) {
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle(title);
        conversation.setMessageCount(0);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());

        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public List<Message> getConversationHistory(Long conversationId) {
        return messageMapper.findByConversationIdOrderByCreateTimeAsc(conversationId);
    }

    @Override
    public List<Conversation> getUserConversations(Long userId) {
        return conversationMapper.findByUserIdOrderByUpdateTimeDesc(userId);
    }

    @Override
    @Transactional
    public boolean deleteConversation(Long conversationId) {
        // 删除会话中的所有消息
        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("conversation_id", conversationId);
        messageMapper.delete(messageQueryWrapper);

        // 删除会话
        conversationMapper.deleteById(conversationId);
        return true;
    }

    @Override
    @Transactional
    public AIResponse regenerateResponse(Long conversationId, Long messageId) {
        // 获取原始消息
        Message originalMessage = messageMapper.selectById(messageId);
        if (originalMessage == null) {
            return new AIResponse("消息不存在");
        }

        // 获取上一条用户消息
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id", conversationId)
                .eq("sender_type", "USER")
                .lt("id", messageId)
                .orderByDesc("id")
                .last("LIMIT 1");

        Message userMessage = messageMapper.selectById(queryWrapper);
        if (userMessage == null) {
            return new AIResponse("未找到对应的用户消息");
        }

        // 获取用户ID
        QueryWrapper<Conversation> convQueryWrapper = new QueryWrapper<>();
        convQueryWrapper.eq("id", conversationId);
        Conversation conversation = conversationMapper.selectOne(convQueryWrapper);
        if (conversation == null) {
            return new AIResponse("会话不存在");
        }

        // 构建AI请求上下文
        AIRequestContext context = buildAIRequestContext(conversation.getUserId(), conversationId);

        // 调用AI服务重新生成回复
        AIResponse aiResponse = aiService.chat(userMessage.getContent(), context);

        // 更新原消息
        originalMessage.setContent(aiResponse.getContent());
        originalMessage.setUpdateTime(LocalDateTime.now());
        messageMapper.update(originalMessage);

        // 设置消息ID
        aiResponse.setMessageId(originalMessage.getId());

        return aiResponse;
    }

    /**
     * 获取或创建会话
     */
    private Conversation getOrCreateConversation(Long userId) {
        QueryWrapper<Conversation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("update_time")
                .last("LIMIT 1");

        Conversation conversation = conversationMapper.selectOne(queryWrapper);

        if (conversation == null) {
            conversation = createConversation(userId, "健康咨询");
        }
        return conversation;
    }

    /**
     * 构建AI请求上下文
     */
    private AIRequestContext buildAIRequestContext(Long userId, Long conversationId) {
        AIRequestContext context = new AIRequestContext();

        // 构建用户健康档案
        context.setHealthProfile(buildHealthProfile(userId));

        // 获取最近的健康指标
        context.setRecentIndicators(getRecentHealthIndicators(userId));

        // 获取对话历史
        context.setConversationHistory(getConversationHistoryForAI(conversationId));

        return context;
    }

    /**
     * 构建用户健康档案
     */
    private HealthProfileContext buildHealthProfile(Long userId) {
        User user = healthProfileMapper.getHealthProfile(userId);
        if (user == null) {
            return null;
        }

        HealthProfileContext profile = new HealthProfileContext();
        profile.setGender(user.getGender());
        profile.setAge(calculateAge(user.getBirthDate()));
        profile.setHeight(user.getHeight() != null ? BigDecimal.valueOf(user.getHeight()) : BigDecimal.ZERO);
        profile.setWeight(user.getWeight() != null ? BigDecimal.valueOf(user.getWeight()) : BigDecimal.ZERO);
        Double bmiValue = calculateBMI(user.getHeight(), user.getWeight());
        profile.setBmi(bmiValue != null ? BigDecimal.valueOf(bmiValue) : BigDecimal.ZERO);
        profile.setBloodType("未知"); // User实体中没有bloodType字段，暂时设为默认值

        // 这里可以从用户设置或其他地方获取慢性病和过敏史
        profile.setChronicDiseases(new ArrayList<>());
        profile.setAllergies(new ArrayList<>());

        return profile;
    }

    /**
     * 获取最近的健康指标
     */
    private List<HealthIndicatorDTO> getRecentHealthIndicators(Long userId) {
        List<HealthIndicatorDTO> indicators = new ArrayList<>();

        // 这里应该从HealthRecordService获取用户的健康记录
        // 由于HealthRecordService没有在当前类中注入，这里先返回空列表
        // 实际项目中应该注入HealthRecordService并调用相关方法

        return indicators;
    }

    /**
     * 获取对话历史（用于AI）
     */
    private List<ConversationMessage> getConversationHistoryForAI(Long conversationId) {
        List<Message> messages = getConversationHistory(conversationId);
        return messages.stream()
                .map(message -> {
                    ConversationMessage msg = new ConversationMessage();
                    msg.setRole("USER".equals(message.getSenderType()) ? "user" : "assistant");
                    msg.setContent(message.getContent());
                    return msg;
                })
                .collect(Collectors.toList());
    }

    /**
     * 计算年龄
     */
    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, java.time.LocalDate.now()).getYears();
    }

    /**
     * 计算BMI
     */
    private Double calculateBMI(Double height, Double weight) {
        if (height == null || weight == null || height <= 0) {
            return null;
        }
        return weight / (height / 100.0 * height / 100.0);
    }

}