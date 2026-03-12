package com.health.service.file.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.MedicalDocument;
import com.health.mapper.MedicalDocumentMapper;
import com.health.service.file.MedicalFileService;
import com.health.utils.OCRUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MedicalFileServiceImpl implements MedicalFileService {

    @Autowired
    private MedicalDocumentMapper medicalDocumentMapper;

    @Autowired
    private OCRUtil ocrUtil;

    @Value("${file.upload.path:/data/uploads/medical/}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:http://localhost:8080/files/}")
    private String urlPrefix;

    @Override
    @Transactional
    public MedicalDocument uploadDocument(MultipartFile file, Long userId, String category, String description)
            throws IOException {
        // 验证文件
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        // 创建日期目录
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fullPath = uploadPath + dateFolder + "/";

        // 确保目录存在
        File directory = new File(fullPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 保存文件
        String filePath = fullPath + fileName;
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        // OCR识别（仅对图片进行OCR）
        String ocrResult = null;
        if (isImageFile(extension)) {
            try {
                ocrResult = ocrUtil.extractTextFromImage(file);
            } catch (Exception e) {
                log.error("OCR识别失败", e);
            }
        }

        // 保存文档信息
        MedicalDocument document = new MedicalDocument();
        document.setUserId(userId);
        document.setDocumentName(originalFilename);
        document.setDocumentType(extension.replace(".", "").toUpperCase());
        document.setFilePath(filePath);
        document.setFileUrl(urlPrefix + dateFolder + "/" + fileName);
        document.setFileSize(file.getSize());
        document.setUploadDate(LocalDateTime.now());
        document.setCategory(category);
        document.setDescription(description);
        document.setOcrResult(ocrResult);

        medicalDocumentMapper.insert(document);

        return document;
    }

    @Override
    public List<MedicalDocument> getUserDocuments(Long userId) {
        QueryWrapper<MedicalDocument> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("upload_date");
        return medicalDocumentMapper.selectList(queryWrapper);
    }

    @Override
    public List<MedicalDocument> getDocumentsByCategory(Long userId, String category) {
        QueryWrapper<MedicalDocument> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("category", category)
                .orderByDesc("upload_date");
        return medicalDocumentMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public boolean deleteDocument(Long documentId) {
        MedicalDocument document = medicalDocumentMapper.selectById(documentId);
        if (document == null) {
            return false;
        }

        // 删除文件
        try {
            File file = new File(document.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
        }

        // 删除数据库记录
        return medicalDocumentMapper.deleteById(documentId) > 0;
    }

    @Override
    public MedicalDocument getDocumentById(Long documentId) {
        return medicalDocumentMapper.selectById(documentId);
    }

    @Override
    public byte[] downloadDocument(Long documentId) throws IOException {
        MedicalDocument document = medicalDocumentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }

        Path path = Paths.get(document.getFilePath());
        return Files.readAllBytes(path);
    }

    @Override
    public boolean updateDocument(MedicalDocument document) {
        return medicalDocumentMapper.updateById(document) > 0;
    }

    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String extension) {
        String ext = extension.toLowerCase();
        return ext.equals(".jpg") || ext.equals(".jpeg") ||
                ext.equals(".png") || ext.equals(".gif") ||
                ext.equals(".bmp");
    }
}
