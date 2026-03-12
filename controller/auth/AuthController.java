package com.health.controller.auth;

import com.health.common.Result;
import com.health.dto.request.ChangePasswordDTO;
import com.health.dto.request.LoginDTO;
import com.health.dto.request.RegisterDTO;
import com.health.entity.User;
import com.health.service.user.UserService;
import com.health.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "用户认证接口")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<Map<String, Object>> register(@Validated @RequestBody RegisterDTO registerDTO) {
        Map<String, Object> result = (Map<String, Object>) userService.register(registerDTO);
        return Result.success(result, "注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = (Map<String, Object>) userService.login(loginDTO);
        return Result.success(result, "登录成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    @ApiOperation("获取当前用户信息")
    public Result<User> getProfile(HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        User user = userService.getById(userId);
        // 清除敏感信息
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    @ApiOperation("更新用户信息")
    public Result<User> updateProfile(@RequestBody User user, HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        User updatedUser = userService.updateProfile(userId, user);
        updatedUser.setPassword(null);
        return Result.success(updatedUser, "更新成功");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    @ApiOperation("修改密码")
    public Result<Void> changePassword(@Validated @RequestBody ChangePasswordDTO dto,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        userService.changePassword(userId, dto.getOldPassword(), dto.getNewPassword());
        return Result.success(null, "密码修改成功");
    }

    /**
     * 从请求中获取用户ID
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}