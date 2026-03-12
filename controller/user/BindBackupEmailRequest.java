package com.health.controller.user;

public class BindBackupEmailRequest {
    private String email;
    private String code;

    // Getter and Setter methods
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
