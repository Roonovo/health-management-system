package com.health.dto.request;

import lombok.Data;

@Data
public class MessageDTO {
    private String content;
    private Long conversationId;
}