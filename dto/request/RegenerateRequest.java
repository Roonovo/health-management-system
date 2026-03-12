// RegenerateRequest.java
package com.health.dto.request;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class RegenerateRequest {
    @NotNull(message = "对话ID不能为空")
    private Long conversationId;

    @NotNull(message = "消息ID不能为空")
    private Long messageId;
}
