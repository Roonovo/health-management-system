package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("admin")
public class Admin {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    @JsonIgnore
    private String password;

    @TableField("real_name")
    private String realName;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("avatar")
    private String avatar;

    @TableField("status")
    private Integer status;

    @TableField("role")
    private String role;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}