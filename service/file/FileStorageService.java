package com.health.service.file;

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
 * 文件存储服务
 * 支持本地存储和云存储（阿里云OSS、腾讯云COS等）
 */
@Service
public class FileStorageService {

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

    /**
     * 上传文件到云存储（示例）
     */
    public String uploadToCloud(MultipartFile file) {
        // 集成阿里云OSS或腾讯云COS
        // 示例代码：
        /*
         * try {
         * OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,
         * accessKeySecret);
         * String objectName = "health-docs/" + UUID.randomUUID().toString() +
         * file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(
         * "."));
         * 
         * ossClient.putObject(bucketName, objectName, file.getInputStream());
         * ossClient.shutdown();
         * 
         * return "https://" + bucketName + "." + endpoint + "/" + objectName;
         * } catch (Exception e) {
         * throw new BusinessException("云存储上传失败: " + e.getMessage());
         * }
         */
        return uploadFile(file); // 当前使用本地存储
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl) {
        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath, filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }
}