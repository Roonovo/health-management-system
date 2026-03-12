package com.health.controller.user;

import lombok.Data;

@Data
public class OcrResult {
    private boolean success;
    private String message;
    private String fileId;
    private String text;
    private Double confidence; // OCR识别置信度
}
