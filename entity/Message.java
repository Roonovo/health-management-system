package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("conversation_id")
    private Long conversationId;

    @TableField("content")
    private String content;

    @TableField("sender_type")
    private String senderType; // USER, AI

    // 这两个字段不存在于数据库表中，仅用于数据传输
    // 移除重复的 @TableField 注解
    @TableField(exist = false) // 只保留一个
    private List<String> healthAdvice;

    @TableField(exist = false) // 只保留一个
    private Object recipe;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}