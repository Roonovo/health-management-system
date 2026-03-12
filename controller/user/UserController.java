package com.health.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.health.common.ApiResponse;
import com.health.service.user.UserService;
import com.health.utils.JwtUtil;

// UserController.java
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserProfileDTO> getUserProfile(@RequestHeader("Authorization") String token) {
        // 解析token获取用户ID
        String userId = JwtUtil.parseToken(token);
        UserProfileDTO profile = userService.getUserProfile(userId);
        return ApiResponse.success(profile);
    }

    @PutMapping("/profile")
    public ApiResponse<Void> updateUserProfile(@RequestHeader("Authorization") String token,
            @RequestBody UserProfileDTO profileDTO) {
        String userId = JwtUtil.parseToken(token);
        userService.updateProfile(userId, profileDTO);
        return ApiResponse.success();
    }
}
