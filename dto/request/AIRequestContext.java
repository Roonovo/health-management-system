// AIRequestContext.java
package com.health.dto.request;

import lombok.Data;
import java.util.List;

import com.health.dto.response.ConversationMessage;
import com.health.dto.response.HealthIndicatorDTO;
import com.health.dto.response.HealthProfileContext;

@Data
public class AIRequestContext {
    private HealthProfileContext healthProfile;
    private List<HealthIndicatorDTO> recentIndicators;
    private List<ConversationMessage> conversationHistory;
}
