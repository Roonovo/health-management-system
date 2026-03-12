package com.health.dto.request;

import lombok.Data;

@Data
public class UserProfileDTO {
    private String name;
    private String email;
    private String phone;
    private String birthday;
    private String gender;
    private Double height;
    private Double weight;
    private HealthRecordDTO healthRecord;

    public Object getBio() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBio'");
    }
}