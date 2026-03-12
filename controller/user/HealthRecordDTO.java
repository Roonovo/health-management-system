package com.health.controller.user;

import lombok.Data;

@Data
public class HealthRecordDTO {
    private String allergies;
    private String chronicDiseases;
    private String medications;
}