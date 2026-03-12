// src/main/java/com/health/service/AdminService.java
package com.health.service.admin;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.Admin;

public interface AdminService extends IService<Admin> {
    Admin login(String username, String password);

    boolean updateLastLogin(Long adminId, String ip);

    boolean changePassword(Long adminId, String oldPassword, String newPassword);
}
