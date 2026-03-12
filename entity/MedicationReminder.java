package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("medication_reminder")
public class MedicationReminder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("plan_id")
    private Long planId;

    @TableField("reminder_time")
    private LocalDateTime reminderTime;

    @TableField("status")
    private String status; // PENDING, SENT, CONFIRMED, MISSED

    @TableField("actual_time")
    private LocalDateTime actualTime;

    @TableField("note")
    private String note;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}