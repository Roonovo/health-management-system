package com.health.service.medication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.dto.request.MedicationPlanDTO;
import com.health.entity.MedicationPlan;
import com.health.entity.MedicationReminder;
import com.health.service.notification.PushNotificationService;
import com.health.exception.BusinessException;
import com.health.mapper.MedicationPlanRepository;
import com.health.mapper.MedicationReminderRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MedicationService {

    @Autowired
    private MedicationPlanRepository medicationPlanRepository;

    @Autowired
    private MedicationReminderRepository medicationReminderRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建用药计划
     */
    @Transactional
    public MedicationPlan createPlan(String userId, MedicationPlanDTO dto) {
        MedicationPlan plan = new MedicationPlan();
        BeanUtils.copyProperties(dto, plan);
        plan.setUserId(Long.valueOf(userId));

        // 将提醒时间列表转换为JSON字符串
        try {
            plan.setReminderTimes(objectMapper.writeValueAsString(dto.getReminderTimes()));
        } catch (Exception e) {
            throw new BusinessException("提醒时间格式错误");
        }

        MedicationPlan savedPlan = medicationPlanRepository.save(plan);

        // 生成提醒记录
        generateReminders(savedPlan);

        return savedPlan;
    }

    /**
     * 生成提醒记录
     */
    private void generateReminders(MedicationPlan plan) {
        try {
            List<String> reminderTimes = objectMapper.readValue(
                    plan.getReminderTimes().toString(),
                    new TypeReference<List<String>>() {
                    });

            LocalDate currentDate = plan.getStartDate();
            LocalDate endDate = plan.getEndDate() != null ? plan.getEndDate() : currentDate.plusDays(30); // 默认30天

            while (!currentDate.isAfter(endDate)) {
                for (String timeStr : reminderTimes) {
                    LocalDateTime reminderTime = parseReminderTime(currentDate, timeStr);

                    // 只创建未来的提醒
                    if (reminderTime.isAfter(LocalDateTime.now())) {
                        MedicationReminder reminder = new MedicationReminder();
                        reminder.setPlanId(plan.getId());
                        reminder.setReminderTime(reminderTime);
                        reminder.setStatus("PENDING");
                        medicationReminderRepository.save(reminder);
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        } catch (Exception e) {
            throw new BusinessException("生成提醒记录失败: " + e.getMessage());
        }
    }

    /**
     * 解析提醒时间
     */
    private LocalDateTime parseReminderTime(LocalDate date, String timeStr) throws ParseException {
        LocalTime time = LocalTime.parse(timeStr);
        return LocalDateTime.of(date, time);
    }

    /**
     * 更新用药计划
     */
    @Transactional
    public MedicationPlan updatePlan(String userId, Long planId, MedicationPlanDTO dto) {
        MedicationPlan plan = medicationPlanRepository.selectById(planId);
        if (plan == null) {
            throw new BusinessException("用药计划不存在");
        }

        if (!plan.getUserId().equals(Long.valueOf(userId))) {
            throw new BusinessException("无权修改该用药计划");
        }

        // 更新计划信息
        if (dto.getMedicationName() != null)
            plan.setMedicationName(dto.getMedicationName());
        if (dto.getDosage() != null)
            plan.setDosage(dto.getDosage());
        if (dto.getFrequency() != null)
            plan.setFrequency(dto.getFrequency());
        if (dto.getNotes() != null)
            plan.setNotes(dto.getNotes());

        // 如果提醒时间或日期范围改变，重新生成提醒
        boolean needRegenerateReminders = false;
        if (dto.getReminderTimes() != null) {
            try {
                plan.setReminderTimes(objectMapper.writeValueAsString(dto.getReminderTimes()));
                needRegenerateReminders = true;
            } catch (Exception e) {
                throw new BusinessException("提醒时间格式错误");
            }
        }
        if (dto.getStartDate() != null) {
            plan.setStartDate(dto.getStartDate());
            needRegenerateReminders = true;
        }
        if (dto.getEndDate() != null) {
            plan.setEndDate(dto.getEndDate());
            needRegenerateReminders = true;
        }

        MedicationPlan savedPlan = medicationPlanRepository.save(plan);

        if (needRegenerateReminders) {
            // 删除旧的未发送提醒
            List<MedicationReminder> oldReminders = medicationReminderRepository.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MedicationReminder>()
                            .eq("plan_id", planId)
                            .eq("status", "PENDING"));
            for (MedicationReminder reminder : oldReminders) {
                medicationReminderRepository.deleteById(reminder.getId());
            }
            // 生成新的提醒
            generateReminders(savedPlan);
        }

        return savedPlan;
    }

    /**
     * 获取用药计划列表
     */
    public List<MedicationPlan> getPlansByUserId(String userId) {
        return medicationPlanRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MedicationPlan>()
                        .eq("user_id", Long.valueOf(userId))
                        .eq("status", 1));
    }

    /**
     * 删除用药计划（软删除）
     */
    @Transactional
    public void deletePlan(String userId, Long planId) {
        MedicationPlan plan = medicationPlanRepository.selectById(planId);
        if (plan == null) {
            throw new BusinessException("用药计划不存在");
        }

        if (!plan.getUserId().equals(Long.valueOf(userId))) {
            throw new BusinessException("无权删除该用药计划");
        }

        plan.setStatus(0);
        medicationPlanRepository.save(plan);

        // 删除未发送的提醒
        List<MedicationReminder> oldReminders = medicationReminderRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MedicationReminder>()
                        .eq("plan_id", planId)
                        .eq("status", "PENDING"));
        for (MedicationReminder reminder : oldReminders) {
            medicationReminderRepository.deleteById(reminder.getId());
        }
    }

    /**
     * 确认服药
     */
    @Transactional
    public void confirmMedication(String userId, Long reminderId) {
        MedicationReminder reminder = medicationReminderRepository.selectById(reminderId);
        if (reminder == null) {
            throw new BusinessException("提醒记录不存在");
        }

        MedicationPlan plan = medicationPlanRepository.selectById(reminder.getPlanId());
        if (plan == null) {
            throw new BusinessException("用药计划不存在");
        }

        if (!plan.getUserId().equals(Long.valueOf(userId))) {
            throw new BusinessException("无权操作该提醒");
        }

        reminder.setStatus("CONFIRMED");
        reminder.setActualTime(LocalDateTime.now());
        medicationReminderRepository.save(reminder);
    }

    /**
     * 获取提醒历史
     */
    public List<MedicationReminder> getReminderHistory(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<MedicationPlan> plans = medicationPlanRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MedicationPlan>()
                        .eq("user_id", Long.valueOf(userId)));
        List<Long> planIds = new ArrayList<>();
        for (MedicationPlan plan : plans) {
            planIds.add(plan.getId());
        }

        if (planIds.isEmpty()) {
            return new ArrayList<>();
        }

        return medicationReminderRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MedicationReminder>()
                        .in("plan_id", planIds)
                        .between("reminder_time", startDate, endDate));
    }

    /**
     * 定时任务：扫描并发送提醒
     */
    @Transactional
    public void scanAndSendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteLater = now.plusMinutes(1);

        List<MedicationReminder> pendingReminders = medicationReminderRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MedicationReminder>()
                        .eq("status", "PENDING")
                        .between("reminder_time", now, oneMinuteLater));

        for (MedicationReminder reminder : pendingReminders) {
            try {
                MedicationPlan plan = medicationPlanRepository.selectById(reminder.getPlanId());

                if (plan != null && plan.getStatus() == 1) {
                    // 发送推送通知
                    pushNotificationService.sendMedicationReminder(
                            plan.getUserId(),
                            plan.getMedicationName(),
                            plan.getDosage());

                    // 更新状态
                    reminder.setStatus("SENT");
                    medicationReminderRepository.save(reminder);
                }
            } catch (Exception e) {
                // 记录错误日志，继续处理下一条
                System.err.println("发送提醒失败: " + e.getMessage());
            }
        }

        // 标记过期未响应的提醒为漏服
        markMissedReminders();
    }

    /**
     * 标记漏服提醒
     */
    @Transactional
    public void markMissedReminders() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        List<MedicationReminder> sentReminders = medicationReminderRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MedicationReminder>()
                        .eq("status", "SENT")
                        .lt("reminder_time", oneHourAgo));

        for (MedicationReminder reminder : sentReminders) {
            reminder.setStatus("MISSED");
            medicationReminderRepository.save(reminder);
        }
    }
}
