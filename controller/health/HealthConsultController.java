package com.health.controller.health;

import com.health.common.Result;
import com.health.dto.request.MessageDTO;
import com.health.dto.response.AIResponse;
import com.health.entity.Conversation;
import com.health.entity.Message;
import com.health.service.health.HealthConsultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "健康咨询")
@RestController
@RequestMapping("/api/health/consult")
@CrossOrigin
public class HealthConsultController {

    @Autowired
    private HealthConsultService healthConsultService;

    @ApiOperation("发送咨询消息")
    @PostMapping("/chat")
    public Result<AIResponse> chat(
            @RequestBody MessageDTO messageDTO,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        AIResponse response = healthConsultService.chat(messageDTO.getContent(), userId);

        return Result.success(response);
    }

    @ApiOperation("获取对话历史")
    @GetMapping("/history/{conversationId}")
    public Result<List<Message>> getHistory(@PathVariable Long conversationId) {
        List<Message> messages = healthConsultService.getConversationHistory(conversationId);
        return Result.success(messages);
    }

    @ApiOperation("获取所有会话")
    @GetMapping("/conversations")
    public Result<List<Conversation>> getConversations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Conversation> conversations = healthConsultService.getUserConversations(userId);
        return Result.success(conversations);
    }

    @ApiOperation("创建新会话")
    @PostMapping("/conversations")
    public Result<Conversation> createConversation(
            @RequestBody MessageDTO messageDTO,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        Conversation conversation = healthConsultService.createConversation(
                userId,
                messageDTO.getContent());

        return Result.success(conversation);
    }

    @ApiOperation("删除会话")
    @DeleteMapping("/conversations/{conversationId}")
    public Result<?> deleteConversation(@PathVariable Long conversationId) {
        boolean success = healthConsultService.deleteConversation(conversationId);

        if (success) {
            return Result.success("会话删除成功");
        }

        return Result.error("删除失败");
    }

    @ApiOperation("重新生成回复")
    @PostMapping("/regenerate/{conversationId}/{messageId}")
    public Result<AIResponse> regenerate(
            @PathVariable Long conversationId,
            @PathVariable Long messageId) {

        AIResponse response = healthConsultService.regenerateResponse(conversationId, messageId);

        return Result.success(response);
    }
}