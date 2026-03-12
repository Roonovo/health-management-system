package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@TableName("medication_plan")
public class MedicationPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("medication_name")
    private String medicationName;

    @TableField("dosage")
    private String dosage;

    @TableField("frequency")
    private String frequency;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("notes")
    private String notes;

    @TableField("status")
    private Integer status; // 0-已停用 1-进行中

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 提醒时间列表（JSON格式存储）
     * 数据库中存储为 VARCHAR/TEXT 类型
     */
    @TableField("reminder_times")
    private String reminderTimes;

    /**
     * 用于临时存储解析后的提醒时间列表（不存储到数据库）
     */
    @TableField(exist = false)
    private List<String> reminderTimeList;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 设置提醒时间（JSON字符串格式）
     * 
     * @param reminderTimesJson JSON格式的提醒时间字符串
     */
    public void setReminderTimes(String reminderTimesJson) {
        this.reminderTimes = reminderTimesJson;

        // 同时解析JSON字符串到列表
        if (reminderTimesJson != null && !reminderTimesJson.trim().isEmpty()) {
            try {
                this.reminderTimeList = objectMapper.readValue(
                        reminderTimesJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (JsonProcessingException e) {
                log.error("解析提醒时间JSON失败: {}", reminderTimesJson, e);
                this.reminderTimeList = new ArrayList<>();
            }
        } else {
            this.reminderTimeList = new ArrayList<>();
        }
    }

    /**
     * 获取提醒时间（JSON字符串格式）
     * 
     * @return JSON格式的提醒时间字符串
     */
    public String getReminderTimes() {
        return this.reminderTimes;
    }

    /**
     * 设置提醒时间列表
     * 
     * @param reminderTimeList 提醒时间列表
     */
    public void setReminderTimeList(List<String> reminderTimeList) {
        this.reminderTimeList = reminderTimeList;

        // 同时转换为JSON字符串
        if (reminderTimeList != null && !reminderTimeList.isEmpty()) {
            try {
                this.reminderTimes = objectMapper.writeValueAsString(reminderTimeList);
            } catch (JsonProcessingException e) {
                log.error("序列化提醒时间列表失败", e);
                this.reminderTimes = "[]";
            }
        } else {
            this.reminderTimes = "[]";
        }
    }

    /**
     * 获取提醒时间列表
     * 
     * @return 提醒时间列表
     */
    public List<String> getReminderTimeList() {
        if (this.reminderTimeList == null && this.reminderTimes != null) {
            setReminderTimes(this.reminderTimes);
        }
        return this.reminderTimeList != null ? this.reminderTimeList : new ArrayList<>();
    }

    /**
     * 设置药品名称（支持Object类型参数）
     * 
     * @param medicineName 药品名称（可以是String或其他类型）
     */
    public void setMedicineName(Object medicineName) {
        if (medicineName == null) {
            this.medicationName = null;
        } else if (medicineName instanceof String) {
            this.medicationName = (String) medicineName;
        } else {
            this.medicationName = medicineName.toString();
        }
    }

    /**
     * 添加单个提醒时间
     * 
     * @param time 提醒时间（格式：HH:mm）
     */
    public void addReminderTime(String time) {
        if (this.reminderTimeList == null) {
            this.reminderTimeList = new ArrayList<>();
        }
        if (time != null && !time.trim().isEmpty()) {
            this.reminderTimeList.add(time);
            setReminderTimeList(this.reminderTimeList);
        }
    }

    /**
     * 移除指定的提醒时间
     * 
     * @param time 要移除的提醒时间
     */
    public void removeReminderTime(String time) {
        if (this.reminderTimeList != null) {
            this.reminderTimeList.remove(time);
            setReminderTimeList(this.reminderTimeList);
        }
    }

    /**
     * 清空所有提醒时间
     */
    public void clearReminderTimes() {
        this.reminderTimeList = new ArrayList<>();
        this.reminderTimes = "[]";
    }

    /**
     * 检查用药计划是否处于激活状态
     * 
     * @return true表示激活，false表示已停用
     */
    public boolean isActive() {
        return this.status != null && this.status == 1;
    }

    /**
     * 检查用药计划是否已过期
     * 
     * @return true表示已过期
     */
    public boolean isExpired() {
        if (this.endDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(this.endDate);
    }

    /**
     * 检查用药计划是否还未开始
     * 
     * @return true表示还未开始
     */
    public boolean isNotStarted() {
        if (this.startDate == null) {
            return false;
        }
        return LocalDate.now().isBefore(this.startDate);
    }

    /**
     * 检查用药计划是否正在进行中
     * 
     * @return true表示正在进行中
     */
    public boolean isOngoing() {
        return isActive() && !isExpired() && !isNotStarted();
    }
}