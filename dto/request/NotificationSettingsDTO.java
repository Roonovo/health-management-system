package com.health.dto.request;

import lombok.Data;

@Data
public class NotificationSettingsDTO {
    private Boolean medicationReminder; // 用药提醒
    private Boolean appointmentReminder; // 预约提醒
    private Boolean healthReportReminder; // 健康报告提醒
    private Boolean systemNotification; // 系统通知
    private String reminderTime; // 提醒时间（如：08:00）
}