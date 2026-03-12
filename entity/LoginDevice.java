package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("login_device")
public class LoginDevice {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("device_name")
    private String deviceName;

    @TableField("device_type")
    private String deviceType; // WEB, ANDROID, IOS

    @TableField("device_id")
    private String deviceId;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("location")
    private String location;

    @TableField("is_current")
    private Boolean isCurrent;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}