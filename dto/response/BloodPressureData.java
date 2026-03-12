package com.health.dto.response;

import java.time.LocalDateTime;

public class BloodPressureData {
    private Long id;
    private Integer systolic;
    private Integer diastolic;
    private LocalDateTime recordDate;

    public BloodPressureData(Long id, Integer systolic, Integer diastolic, LocalDateTime recordDate) {
        this.id = id;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.recordDate = recordDate;
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }
}
