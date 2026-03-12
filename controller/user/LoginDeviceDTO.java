package com.health.controller.user;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginDeviceDTO {
    private Long id;
    private String deviceName;
    private String deviceType;
    private String deviceId;
    private String ipAddress;
    private String location;
    private Boolean isCurrent;
    private LocalDateTime lastLoginTime;
}
