// UserSettingsController.java
package com.health.controller.user;

import com.health.dto.request.NotificationSettingsDTO;
import com.health.dto.request.PrivacySettingsDTO;
import com.health.service.user.AvatarUploadResponse;
import com.health.service.user.ChangePasswordRequest;
import com.health.service.user.ChangePhoneRequest;
import com.health.service.user.UserSettingsService;
import com.health.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "用户设置管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @ApiOperation("获取用户资料")
    @GetMapping("/profile")
    public Result<UserProfileDTO> getProfile() {
        UserProfileDTO profile = userSettingsService.getProfile();
        return Result.success(profile);
    }

    @ApiOperation("更新用户资料")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UserProfileDTO profile) {
        userSettingsService.updateProfile(profile);
        return Result.success(null);
    }

    @ApiOperation("上传头像")
    @PostMapping("/avatar")
    public Result<AvatarUploadResponse> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        AvatarUploadResponse response = userSettingsService.uploadAvatar(file);
        return Result.success(response);
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userSettingsService.changePassword(request);
        return Result.success(null);
    }

    @ApiOperation("修改手机号")
    @PutMapping("/phone")
    public Result<Void> changePhone(@Valid @RequestBody ChangePhoneRequest request) {
        userSettingsService.changePhone(request);
        return Result.success(null);
    }

    @ApiOperation("修改邮箱")
    @PutMapping("/email")
    public Result<Void> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        userSettingsService.changeEmail(request);
        return Result.success(null);
    }

    @ApiOperation("绑定备用邮箱")
    @PostMapping("/backup-email")
    public Result<Void> bindBackupEmail(@Valid @RequestBody BindBackupEmailRequest request) {
        userSettingsService.bindBackupEmail(request);
        return Result.success();
    }

    @ApiOperation("获取安全设置")
    @GetMapping("/security-settings")
    public Result<SecuritySettingsDTO> getSecuritySettings() {
        SecuritySettingsDTO settings = userSettingsService.getSecuritySettings();
        return Result.success(settings);
    }

    @ApiOperation("设置登录异常提醒")
    @PutMapping("/login-alert")
    public Result<Void> setLoginAlert(@RequestBody LoginAlertRequest request) {
        userSettingsService.setLoginAlert(request.getEnabled());
        return Result.success();
    }

    @ApiOperation("获取登录设备列表")
    @GetMapping("/login-devices")
    public Result<List<LoginDeviceDTO>> getLoginDevices() {
        List<LoginDeviceDTO> devices = userSettingsService.getLoginDevices();
        return Result.success(devices);
    }

    @ApiOperation("移除登录设备")
    @DeleteMapping("/login-devices/{deviceId}")
    public Result<Void> removeLoginDevice(@PathVariable Long deviceId) {
        userSettingsService.removeLoginDevice(deviceId);
        return Result.success();
    }

    @ApiOperation("获取通知设置")
    @GetMapping("/notification-settings")
    public Result<NotificationSettingsDTO> getNotificationSettings() {
        NotificationSettingsDTO settings = userSettingsService.getNotificationSettings();
        return Result.success(settings);
    }

    @ApiOperation("更新通知设置")
    @PutMapping("/notification-settings")
    public Result<Void> updateNotificationSettings(@RequestBody NotificationSettingsDTO settings) {
        userSettingsService.updateNotificationSettings(settings);
        return Result.success();
    }

    @ApiOperation("获取隐私设置")
    @GetMapping("/privacy-settings")
    public Result<PrivacySettingsDTO> getPrivacySettings() {
        PrivacySettingsDTO settings = userSettingsService.getPrivacySettings();
        return Result.success(settings);
    }

    @ApiOperation("更新隐私设置")
    @PutMapping("/privacy-settings")
    public Result<Void> updatePrivacySettings(@RequestBody PrivacySettingsDTO settings) {
        userSettingsService.updatePrivacySettings(settings);
        return Result.success();
    }

    @ApiOperation("导出用户数据")
    @GetMapping("/export-data")
    public ResponseEntity<Resource> exportUserData() {
        Resource resource = userSettingsService.exportUserData();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=health-data.zip")
                .body(resource);
    }

    @ApiOperation("注销账户")
    @PostMapping("/delete-account")
    public Result<Void> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        userSettingsService.deleteAccount(request);
        return Result.success();
    }
}