package com.health.controller.user;

import com.health.common.Result;
import com.health.dto.request.UpdateProfileDTO;
import com.health.entity.User;
import com.health.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "用户资料管理")
@RestController
@RequestMapping("/api/user/profile")
@CrossOrigin
public class UserProfileController {

    @Autowired
    private UserService userService;

    @ApiOperation("获取用户资料")
    @GetMapping
    public Result<User> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);

        if (user != null) {
            user.setPassword(null); // 不返回密码
            return Result.success(user);
        }

        return Result.error("用户不存在");
    }

    @ApiOperation("更新用户资料")
    @PutMapping
    public Result<?> updateProfile(
            @RequestBody UpdateProfileDTO dto,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        User user = new User();
        user.setId(userId);
        BeanUtils.copyProperties(dto, user);

        boolean success = userService.updateUser(user);

        if (success) {
            return Result.success("资料更新成功");
        }

        return Result.error("更新失败");
    }

    @ApiOperation("上传头像")
    @PostMapping("/avatar")
    public Result<?> uploadAvatar(
            @RequestParam String avatarUrl,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);

        boolean success = userService.updateById(user);

        if (success) {
            return Result.success(avatarUrl, "头像更新成功");
        }

        return Result.error("更新失败");
    }
}
