package com.health.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileDTO {
    private String nickname;
    private String avatar;
    private String gender;
    private LocalDate birthDate;
    private Double height;
    private Double weight;
    private String bloodType;
    private String allergies;
    private String chronicDiseases;
}