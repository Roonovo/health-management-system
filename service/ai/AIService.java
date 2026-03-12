// AIService.java (AI服务接口)
package com.health.service.ai;

import com.health.dto.request.AIRequestContext;
import com.health.dto.response.AIResponse;
import com.health.dto.response.RecipeDTO;
import com.health.dto.response.HealthProfileContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI服务 - 可集成第三方AI API (如OpenAI, Claude等)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;

    @Value("${ai.api.url:}")
    private String aiApiUrl;

    @Value("${ai.api.key:}")
    private String aiApiKey;

    @Value("${ai.model:gpt-3.5-turbo}")
    private String aiModel;

    /**
     * 发送聊天请求到AI服务
     */
    public AIResponse chat(String userMessage, AIRequestContext context) {
        try {
            // 构建系统提示词
            String systemPrompt = buildSystemPrompt(context);

            // 构建请求
            Map<String, Object> request = new HashMap<>();
            request.put("model", aiModel);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);

            // 添加历史对话
            if (context.getConversationHistory() != null) {
                context.getConversationHistory()
                        .forEach(msg -> {
                            Map<String, String> historyMessage = new HashMap<>();
                            historyMessage.put("role", msg.getRole());
                            historyMessage.put("content", msg.getContent());
                            messages.add(historyMessage);
                        });
            }

            // 添加当前用户消息
            Map<String, String> userMessageMap = new HashMap<>();
            userMessageMap.put("role", "user");
            userMessageMap.put("content", userMessage);
            messages.add(userMessageMap);

            request.put("messages", messages);
            request.put("temperature", 0.7);
            request.put("max_tokens", 1000);

            // 发送请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            Map<String, Object> response = restTemplate.postForObject(
                    aiApiUrl + "/chat/completions",
                    entity,
                    Map.class);

            // 解析响应
            return parseAIResponse(response, context);

        } catch (Exception e) {
            log.error("AI服务调用失败", e);
            // 返回默认回复
            return createDefaultResponse();
        }
    }

    /**
     * 生成对话标题
     */
    public String generateConversationTitle(String firstMessage) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", aiModel);
            List<Map<String, String>> titleMessages = new ArrayList<>();
            Map<String, String> systemTitleMessage = new HashMap<>();
            systemTitleMessage.put("role", "system");
            systemTitleMessage.put("content", "请根据用户的第一条消息，生成一个简短的对话标题（不超过20个字）");
            titleMessages.add(systemTitleMessage);

            Map<String, String> userTitleMessage = new HashMap<>();
            userTitleMessage.put("role", "user");
            userTitleMessage.put("content", firstMessage);
            titleMessages.add(userTitleMessage);

            request.put("messages", titleMessages);
            request.put("max_tokens", 50);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            Map<String, Object> response = restTemplate.postForObject(
                    aiApiUrl + "/chat/completions",
                    entity,
                    Map.class);

            return extractContentFromResponse(response);

        } catch (Exception e) {
            log.error("生成对话标题失败", e);
            return "健康咨询 " + new Date().toString();
        }
    }

    /**
     * 从图片中提取文本 (OCR)
     */
    public String extractTextFromImage(String imageUrl) {
        try {
            // 这里可以集成OCR服务，如Tesseract、百度OCR等
            // 简化处理
            return "";
        } catch (Exception e) {
            log.error("图片文本提取失败", e);
            return "";
        }
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(AIRequestContext context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的AI健康助手，负责为用户提供健康咨询服务。\n\n");
        prompt.append("重要提示：\n");
        prompt.append("1. 你提供的建议仅供参考，不能替代专业医疗诊断\n");
        prompt.append("2. 遇到严重健康问题时，建议用户及时就医\n");
        prompt.append("3. 回答要专业、准确、易懂\n");
        prompt.append("4. 可以根据用户的健康档案提供个性化建议\n\n");

        // 添加用户健康档案信息
        if (context.getHealthProfile() != null) {
            prompt.append("用户健康档案：\n");
            HealthProfileContext profile = context.getHealthProfile();
            prompt.append(String.format("- 性别：%s\n", profile.getGender()));
            prompt.append(String.format("- 年龄：%d岁\n", profile.getAge()));
            prompt.append(String.format("- 身高：%.1fcm\n", profile.getHeight()));
            prompt.append(String.format("- 体重：%.1fkg\n", profile.getWeight()));
            prompt.append(String.format("- BMI:%.1f\n", profile.getBmi()));

            if (profile.getChronicDiseases() != null && !profile.getChronicDiseases().isEmpty()) {
                prompt.append(String.format("- 慢性病史：%s\n", String.join("、", profile.getChronicDiseases())));
            }

            if (profile.getAllergies() != null && !profile.getAllergies().isEmpty()) {
                prompt.append(String.format("- 过敏史：%s\n", String.join("、", profile.getAllergies())));
            }

            prompt.append("\n");
        }

        // 添加最近健康指标
        if (context.getRecentIndicators() != null && !context.getRecentIndicators().isEmpty()) {
            prompt.append("最近健康指标：\n");
            context.getRecentIndicators().forEach(indicator -> prompt.append(String.format("- %s:%s(%s)\n",
                    indicator.getType(),
                    indicator.getValue(),
                    indicator.getDate())));
            prompt.append("\n");
        }

        prompt.append("请根据以上信息，为用户提供专业的健康建议。\n");
        prompt.append("如果适合，可以提供健康食谱、运动建议等个性化内容。\n");

        return prompt.toString();
    }

    /**
     * 解析AI响应
     */
    private AIResponse parseAIResponse(Map<String, Object> response, AIRequestContext context) {
        String content = extractContentFromResponse(response);

        AIResponse aiResponse = new AIResponse();
        aiResponse.setContent(content);

        // 根据内容分析是否包含健康建议
        if (content.contains("建议") || content.contains("推荐")) {
            aiResponse.setHealthAdvice(extractHealthAdvice(content));
            aiResponse.setAdviceReason(extractAdviceReason(content, context));
        }

        // 如果提到食谱，生成食谱信息
        if (content.contains("食谱") || content.contains("饮食")) {
            aiResponse.setRecipe((RecipeDTO) generateRecipe(content, context));
        }

        return aiResponse;
    }

    private String extractContentFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
        }
        return "";
    }

    private List<String> extractHealthAdvice(String content) {
        // 简单提取建议（实际应使用NLP技术）
        List<String> advice = new ArrayList<>();
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.contains("建议") || line.matches("^\\d+\\..*")) {
                advice.add(line.trim());
            }
        }
        return advice.isEmpty() ? null : advice;
    }

    private String extractAdviceReason(String content, AIRequestContext context) {
        if (context.getHealthProfile() != null) {
            return String.format("根据您的BMI指数%.1f和健康状况", context.getHealthProfile().getBmi());
        }
        return "基于健康知识库分析";
    }

    private Map<String, Object> generateRecipe(String content, AIRequestContext context) {
        // 简化处理，实际应调用AI生成完整食谱
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("name", "健康食谱");
        recipe.put("tags", Arrays.asList("健康", "营养"));
        recipe.put("calories", 500);
        recipe.put("protein", 30);
        recipe.put("fat", 15);
        recipe.put("ingredients", Arrays.asList("鸡胸肉", "西兰花", "糙米"));
        recipe.put("steps", Arrays.asList(
                "准备食材，鸡胸肉切块",
                "西兰花洗净切小朵",
                "煮糙米饭",
                "鸡胸肉用少量油煎至金黄",
                "西兰花焯水后与鸡肉一起摆盘"));
        return recipe;
    }

    private AIResponse createDefaultResponse() {
        AIResponse response = new AIResponse();
        response.setContent("抱歉，我暂时无法回答您的问题。请稍后再试，或咨询专业医生。");
        return response;
    }
}
