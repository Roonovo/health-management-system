package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("health_record")
public class HealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("record_date")
    private LocalDateTime recordDate;

    @TableField("blood_pressure_systolic")
    private Integer bloodPressureSystolic;

    @TableField("blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;

    @TableField("heart_rate")
    private Integer heartRate;

    @TableField("blood_sugar")
    private Double bloodSugar;

    @TableField("weight")
    private Double weight;

    @TableField("height")
    private Double height;

    @TableField("temperature")
    private Double temperature;

    @TableField("notes")
    private String notes;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("file_url")
    private String fileUrl;

    @TableField("family_history")
    private String familyHistory;

    @TableField("medical_history")
    private String medicalHistory;

    @TableField("allergies")
    private String allergies;

    @TableField("age")
    private Integer age;

    @TableField("blood_type")
    private String bloodType;
}