package com.health.controller.user;

import lombok.Data;

@Data
public class FileUploadResult {
    private boolean success;
    private String message;
    private String fileId;
    private String fileName;
    private String fileUrl;
}
