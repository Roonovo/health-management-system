package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user") // 复用user表
public class HealthProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("height")
    private Double height;

    @TableField("weight")
    private Double weight;

    @TableField("gender")
    private String gender;

    @TableField("birth_date")
    private LocalDate birthDate;

    @TableField("blood_type")
    private String bloodType;

    @TableField("allergies")
    private String allergies;

    @TableField("chronic_diseases")
    private String chronicDiseases;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}