// HealthDataRequest.java
package com.health.dto.request;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HealthDataRequest {
    @NotNull(message = "记录日期不能为空")
    private LocalDateTime recordDate;

    private BigDecimal value;

    private Integer systolic;

    private Integer diastolic;

    private String measurementTime;

    private String note;

    // Getter and Setter methods
    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

    public String getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(String measurementTime) {
        this.measurementTime = measurementTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
