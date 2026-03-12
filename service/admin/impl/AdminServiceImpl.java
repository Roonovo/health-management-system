// src/main/java/com/health/service/impl/AdminServiceImpl.java
package com.health.service.admin.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.Admin;
import com.health.mapper.AdminMapper;
import com.health.service.admin.AdminService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Admin login(String username, String password) {
        Admin admin = baseMapper.findByUsername(username);
        if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
            return admin;
        }
        return null;
    }

    @Override
    public boolean updateLastLogin(Long adminId, String ip) {
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setLastLoginTime(LocalDateTime.now());
        admin.setLastLoginIp(ip);
        return updateById(admin);
    }

    @Override
    public boolean changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = getById(adminId);
        if (admin != null && passwordEncoder.matches(oldPassword, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(newPassword));
            return updateById(admin);
        }
        return false;
    }
}