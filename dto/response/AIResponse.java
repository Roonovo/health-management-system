package com.health.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class AIResponse {
    private String content;
    private List<String> healthAdvice;
    private RecipeDTO recipe;
    private Long messageId;

    public AIResponse() {
    }

    public AIResponse(String content) {
        this.content = content;
    }

    public void setAdviceReason(String adviceReason) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAdviceReason'");
    }
}