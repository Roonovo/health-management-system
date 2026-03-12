package com.health.controller.user;

public class UserProfileDTO {
    private String name;
    private String email;
    private String phone;
    private String birthday;
    private String gender;
    private Double height;
    private Double weight;
    private HealthRecordDTO healthRecord;

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public HealthRecordDTO getHealthRecord() {
        return healthRecord;
    }

    public void setHealthRecord(HealthRecordDTO healthRecord) {
        this.healthRecord = healthRecord;
    }

    public Object getBio() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBio'");
    }
}
