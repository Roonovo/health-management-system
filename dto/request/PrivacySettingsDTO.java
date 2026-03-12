package com.health.dto.request;

import lombok.Data;

@Data
public class PrivacySettingsDTO {
    private Boolean profileVisibility; // 个人资料可见性
    private Boolean healthDataVisibility; // 健康数据可见性
    private Boolean allowDataAnalysis; // 允许数据分析
    private Boolean allowThirdPartySharing; // 允许第三方共享
}
