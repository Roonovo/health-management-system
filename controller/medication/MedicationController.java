package com.health.controller.medication;

import com.health.common.Result;
import com.health.dto.request.MedicationPlanDTO;
import com.health.entity.MedicationPlan;
import com.health.entity.MedicationReminder;
import com.health.service.medication.MedicationService;
import com.health.utils.JwtUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/medications")
@Api(tags = "用药管理接口")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 创建用药计划
     */
    @PostMapping("/plans")
    @ApiOperation("创建用药计划")
    public Result<MedicationPlan> createPlan(@Validated @RequestBody MedicationPlanDTO dto,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        MedicationPlan plan = medicationService.createPlan(userId, dto);
        return Result.success(plan, "用药计划创建成功");
    }

    /**
     * 更新用药计划
     */
    @PutMapping("/plans/{planId}")
    @ApiOperation("更新用药计划")
    public Result<MedicationPlan> updatePlan(@PathVariable Long planId,
            @RequestBody MedicationPlanDTO dto,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        MedicationPlan plan = medicationService.updatePlan(userId, planId, dto);
        return Result.success(plan, "用药计划更新成功");
    }

    /**
     * 获取用药计划列表
     */
    @GetMapping("/plans")
    @ApiOperation("获取用药计划列表")
    public Result<List<MedicationPlan>> getPlans(HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        List<MedicationPlan> plans = medicationService.getPlansByUserId(userId);
        return Result.success(plans);
    }

    /**
     * 删除用药计划
     */
    @DeleteMapping("/plans/{planId}")
    @ApiOperation("删除用药计划")
    public Result<Void> deletePlan(@PathVariable Long planId,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        medicationService.deletePlan(userId, planId);
        return Result.success(null, "用药计划删除成功");
    }

    /**
     * 确认服药
     */
    @PostMapping("/reminders/{reminderId}/confirm")
    @ApiOperation("确认服药")
    public Result<Void> confirmMedication(@PathVariable Long reminderId,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        medicationService.confirmMedication(userId, reminderId);
        return Result.success(null, "服药已确认");
    }

    /**
     * 获取提醒历史
     */
    @GetMapping("/reminders/history")
    @ApiOperation("获取提醒历史")
    public Result<List<MedicationReminder>> getReminderHistory(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);

        // Convert Date to LocalDateTime
        LocalDateTime startDateTime = startDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime endDateTime = endDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        List<MedicationReminder> reminders = medicationService.getReminderHistory(
                userId, startDateTime, endDateTime);
        return Result.success(reminders);
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