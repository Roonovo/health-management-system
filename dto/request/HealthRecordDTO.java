package com.health.dto.request;

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
}
