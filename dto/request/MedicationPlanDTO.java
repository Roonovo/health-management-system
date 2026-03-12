package com.health.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicationPlanDTO {

    private String medicationName; // 药品名称

    private String dosage; // 剂量

    private String frequency; // 频率（如：每日两次）

    private LocalDate startDate; // 开始日期

    private LocalDate endDate; // 结束日期

    private String notes; // 备注

    public Object getReminderTimes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReminderTimes'");
    }

    public Object getMedicineName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMedicineName'");
    }
}
