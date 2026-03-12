package com.health.controller.health;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthRecordDTO {

    private LocalDateTime recordDate;

    private Integer bloodPressureSystolic; // 收缩压

    private Integer bloodPressureDiastolic; // 舒张压

    private Integer heartRate; // 心率

    private Double bloodSugar; // 血糖

    private Double weight; // 体重

    private Double height; // 身高

    private Double temperature; // 体温

    private String notes; // 备注

    // Additional fields for health record management
    private String name; // 姓名
    private Integer age; // 年龄
    private String gender; // 性别
    private String bloodType; // 血型
    private String allergies; // 过敏史
    private String medicalHistory; // 既往病史
    private String familyHistory; // 家族病史
}
