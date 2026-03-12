// ChatRequest.java
package com.health.dto.request;

import lombok.Data;
import java.util.List;

import com.health.dto.response.HealthIndicatorDTO;
import com.health.dto.response.HealthProfileContext;

@Data
public class ChatRequest {
    private Long conversationId;
    private String message;
    private ContextData context;

    @Data
    public static class ContextData {
        private HealthProfileContext healthProfile;
        private List<HealthIndicatorDTO> recentIndicators;
    }
}