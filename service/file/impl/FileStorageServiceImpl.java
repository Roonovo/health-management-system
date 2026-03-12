package com.health.service.file.impl;

import com.health.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件存储服务实现类
 */
@Service
public class FileStorageServiceImpl {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.access.url}")
    private String accessUrl;

    /**
     * 上传文件到本地存储
     */
    public String uploadFile(MultipartFile file) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 返回访问URL
            return accessUrl + "/" + filename;

        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
}
