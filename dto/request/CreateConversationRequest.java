// CreateConversationRequest.java
package com.health.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CreateConversationRequest {
    @NotBlank(message = "对话标题不能为空")
    private String title;
}
