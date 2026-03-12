package com.health.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {

    private String phone;

    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String verificationCode; // 验证码登录
}