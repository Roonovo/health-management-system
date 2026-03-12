package com.health.service.health;

import com.health.entity.HealthRecord;
import com.health.entity.MedicalDocument;
import com.health.exception.BusinessException;
import com.health.mapper.HealthRecordRepository;
import com.health.mapper.MedicalDocumentMapper;
import com.health.service.file.FileStorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private MedicalDocumentMapper medicalDocumentMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ocrUtil ocrUtil;

    /**
     * 创建健康档案
     */
    @Transactional
    public HealthRecord createRecord(Long userId, HealthRecordDTO dto) {
        // 检查用户是否已有档案
        Optional<HealthRecord> existingRecord = healthRecordRepository.findByUserId(userId);
        if (existingRecord.isPresent()) {
            throw new BusinessException("用户已有健康档案，请使用更新接口");
        }

        HealthRecord record = new HealthRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(userId);

        return healthRecordRepository.save(record);
    }

    /**
     * 更新健康档案
     */
    @Transactional
    public HealthRecord updateRecord(Long userId, HealthRecordDTO dto) {
        HealthRecord record = healthRecordRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("健康档案不存在"));

        // 更新非空字段
        if (dto.getWeight() != null)
            record.setWeight(dto.getWeight());
        if (dto.getBloodPressureSystolic() != null)
            record.setBloodPressureSystolic(dto.getBloodPressureSystolic());
        if (dto.getBloodPressureDiastolic() != null)
            record.setBloodPressureDiastolic(dto.getBloodPressureDiastolic());
        if (dto.getBloodSugar() != null)
            record.setBloodSugar(dto.getBloodSugar());
        if (dto.getHeartRate() != null)
            record.setHeartRate(dto.getHeartRate());
        if (dto.getRecordDate() != null)
            record.setRecordDate(java.time.LocalDateTime.parse(dto.getRecordDate()));

        return healthRecordRepository.save(record);
    }

    /**
     * 获取健康档案
     */
    public HealthRecord getRecordByUserId(Long userId) {
        return healthRecordRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("健康档案不存在"));
    }

    /**
     * 上传医疗文档
     */
    @Transactional
    public MedicalDocument uploadDocument(Long userId, MultipartFile file) {
        // 获取健康档案
        HealthRecord record = getRecordByUserId(userId);

        // 验证文件类型
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new BusinessException("不支持的文件格式，仅支持PDF、JPG、PNG");
        }

        // 上传文件到云存储
        String fileUrl = fileStorageService.uploadFile(file);

        // 创建文档记录
        MedicalDocument document = new MedicalDocument();
        document.setRecordId(record.getId());
        document.setFileName(file.getOriginalFilename());
        document.setFileUrl(fileUrl);
        document.setFileType(contentType);
        document.setFileSize(file.getSize());

        // 如果是图片，进行OCR识别
        if (contentType.startsWith("image/")) {
            try {
                String ocrResult = ocrUtil.recognizeText(file);
                document.setOcrResult(ocrResult);
            } catch (Exception e) {
                // OCR失败不影响文件上传
                document.setOcrResult("OCR识别失败: " + e.getMessage());
            }
        }

        medicalDocumentMapper.insert(document);
        return document;
    }

    /**
     * 获取医疗文档列表
     */
    public Optional<List<MedicalDocument>> getDocumentsByUserId(Long userId) {
        List<MedicalDocument> documents = medicalDocumentMapper.findByUserId(userId);
        return Optional.ofNullable(documents);
    }

    /**
     * 删除医疗文档
     */
    @Transactional
    public void deleteDocument(Long userId, Long documentId) {
        MedicalDocument document = medicalDocumentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 验证文档所属用户
        HealthRecord record = healthRecordRepository.selectById(document.getRecordId());
        if (record == null) {
            throw new BusinessException("档案不存在");
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该文档");
        }

        // 删除云存储文件
        fileStorageService.deleteFile(document.getFileUrl());

        // 删除数据库记录
        medicalDocumentMapper.deleteById(documentId);
    }

    /**
     * 验证文件类型
     */
    private boolean isValidFileType(String contentType) {
        return contentType != null && (contentType.equals("application/pdf") ||
                contentType.equals("image/jpeg") ||
                contentType.equals("image/png"));
    }
}
