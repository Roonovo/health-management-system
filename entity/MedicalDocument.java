package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@TableName("medical_document")
public class MedicalDocument {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("document_name")
    private String documentName;

    @TableField("document_type")
    private String documentType;

    @TableField("file_path")
    private String filePath;

    @TableField("file_url")
    private String fileUrl; // 添加这个字段

    @TableField("file_size")
    private Long fileSize;

    @TableField("upload_date")
    private LocalDateTime uploadDate;

    @TableField("category")
    private String category;

    @TableField("description")
    private String description;

    @TableField("ocr_result")
    private String ocrResult; // 存储OCR结果

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 业务方法
    public Long getRecordId() {
        return this.id;
    }

    public void setRecordId(Long recordId) {
        this.id = recordId;
    }

    public String getFileName() {
        return this.documentName;
    }

    public void setFileName(String fileName) {
        this.documentName = fileName;
    }

    public String getFileType() {
        return this.documentType;
    }

    public void setFileType(String fileType) {
        this.documentType = fileType;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setOcrResult(String ocrResult) {
        this.ocrResult = ocrResult;
    }

    public String getOcrResult() {
        return this.ocrResult;
    }

    public static void delete(HealthRecord document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    public static Optional<HealthRecord> findById(Long documentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    public static MedicalDocument save(MedicalDocument document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }
}
