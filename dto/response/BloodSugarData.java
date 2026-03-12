package com.health.dto.response;

import java.time.LocalDateTime;

public class BloodSugarData {
    private Long id;
    private Double bloodSugar;
    private LocalDateTime recordDate;

    public BloodSugarData(Long id, Double bloodSugar, LocalDateTime recordDate) {
        this.id = id;
        this.bloodSugar = bloodSugar;
        this.recordDate = recordDate;
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(Double bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }
}
