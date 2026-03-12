// src/main/java/com/health/controller/AdminController.java
package com.health.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.Result;
import com.health.entity.Admin;
import com.health.entity.User;
import com.health.service.admin.AdminService;
import com.health.service.user.UserService;
import com.health.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "管理员管理")
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<Map> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Admin admin = adminService.login(request.getUsername(), request.getPassword());

        if (admin != null) {
            // 更新最后登录信息
            String ip = getClientIp(httpRequest);
            adminService.updateLastLogin(admin.getId(), ip);

            // 生成Token
            String token = jwtUtil.generateToken(admin.getId().toString(), "ADMIN");

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("adminInfo", admin);

            return Result.success(data, "登录成功");
        }

        return Result.error("用户名或密码错误");
    }

    @ApiOperation("获取管理员信息")
    @GetMapping("/info")
    public Result<Admin> getAdminInfo(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        Admin admin = adminService.getById(adminId);

        if (admin != null) {
            admin.setPassword(null); // 不返回密码
            return Result.success(admin);
        }

        return Result.error("管理员不存在");
    }

    @ApiOperation("修改管理员密码")
    @PutMapping("/password")
    public Result<String> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        boolean success = adminService.changePassword(adminId, oldPassword, newPassword);

        if (success) {
            return Result.success("密码修改成功");
        }

        return Result.error("原密码错误");
    }

    @ApiOperation("获取用户列表")
    @GetMapping("/users")
    public Result getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        Page<User> userPage = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("nickname", keyword)
                    .or()
                    .like("email", keyword)
                    .or()
                    .like("phone", keyword);
        }

        queryWrapper.orderByDesc("create_time");
        Page<User> result = userService.page(userPage, queryWrapper);

        // 移除敏感信息
        result.getRecords().forEach(user -> user.setPassword(null));

        return Result.success(result);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/users/{userId}")
    public Result<User> getUserDetail(@PathVariable Long userId) {
        User user = userService.getById(userId);

        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }

        return Result.error("用户不存在");
    }

    @ApiOperation("禁用/启用用户")
    @PutMapping("/users/{userId}/status")
    public Result updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, Integer> params) {
        User user = new User();
        user.setId(userId);
        user.setStatus(params.get("status"));

        boolean success = userService.updateById(user);

        if (success) {
            return Result.success("用户状态更新成功");
        }

        return Result.error("更新失败");
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/users/{userId}")
    public Result deleteUser(@PathVariable Long userId) {
        boolean success = userService.removeById(userId);

        if (success) {
            return Result.success("用户删除成功");
        }

        return Result.error("删除失败");
    }

    @ApiOperation("获取系统统计数据")
    @GetMapping("/statistics")
    public Result<Map> getStatistics() {
        Map<String, Object> data = new HashMap<>();

        // 用户总数
        long totalUsers = userService.count();
        data.put("totalUsers", totalUsers);

        // 今日新增用户
        QueryWrapper<User> todayWrapper = new QueryWrapper<>();
        todayWrapper.apply("DATE(create_time) = CURDATE()");
        long todayNewUsers = userService.count(todayWrapper);
        data.put("todayNewUsers", todayNewUsers);

        // 活跃用户（最近7天登录）
        QueryWrapper<User> activeWrapper = new QueryWrapper<>();
        activeWrapper.apply("last_login_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)");
        long activeUsers = userService.count(activeWrapper);
        data.put("activeUsers", activeUsers);

        // 禁用用户
        QueryWrapper<User> disabledWrapper = new QueryWrapper<>();
        disabledWrapper.eq("status", 0);
        long disabledUsers = userService.count(disabledWrapper);
        data.put("disabledUsers", disabledUsers);

        return Result.success(data);
    }

    @ApiOperation("获取用户增长趋势")
    @GetMapping("/statistics/user-growth")
    public Result<List> getUserGrowth(@RequestParam(defaultValue = "7") Integer days) {
        List<Map<String, Object>> growth = userService.getUserGrowthStatistics(days);
        return Result.success(growth);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}