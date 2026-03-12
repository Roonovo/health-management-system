package com.health.controller.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.health.dto.response.MedicalFileDTO;
import com.health.entity.MedicalDocument;
import com.health.exception.BusinessException;
import com.health.service.file.MedicalFileService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MedicalFileControllerService {

    @Autowired
    private MedicalFileService fileService;

    /**
     * 获取用户的所有医疗文件
     */
    public List<MedicalFileDTO> getUserFiles(String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            List<MedicalDocument> documents = fileService.getUserDocuments(userIdLong);

            return documents.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            log.error("无效的用户ID格式: {}", userId);
            throw new BusinessException("用户ID格式错误");
        }
    }

    /**
     * 保存已存在的文件（通过文件ID）
     */
    public FileUploadResult saveFile1(String userId, String fileId) {
        FileUploadResult result = new FileUploadResult();

        try {
            Long userIdLong = Long.parseLong(userId);
            Long fileIdLong = Long.parseLong(fileId);

            log.info("保存文件 - 用户ID: {}, 文件ID: {}", userId, fileId);

            // 获取文档
            MedicalDocument document = fileService.getDocumentById(fileIdLong);

            if (document == null) {
                result.setSuccess(false);
                result.setMessage("文件不存在");
                result.setFileId(fileId);
                log.warn("文件不存在 - 文件ID: {}", fileId);
                return result;
            }

            // 验证文件所有权
            if (!document.getUserId().equals(userIdLong)) {
                result.setSuccess(false);
                result.setMessage("权限不足：该文件不属于当前用户");
                result.setFileId(fileId);
                log.warn("权限不足 - 用户ID: {}, 文件ID: {}, 文件所有者ID: {}",
                        userId, fileId, document.getUserId());
                return result;
            }

            // 更新文档的最后访问时间
            document.setUpdateTime(LocalDateTime.now());
            boolean updated = fileService.updateDocument(document);

            if (updated) {
                result.setSuccess(true);
                result.setMessage("文件保存成功");
                result.setFileId(fileId);
                result.setFileName(document.getDocumentName());
                log.info("文件保存成功 - 文件ID: {}, 文件名: {}", fileId, document.getDocumentName());
            } else {
                result.setSuccess(false);
                result.setMessage("文件保存失败");
                result.setFileId(fileId);
                log.error("文件保存失败 - 文件ID: {}", fileId);
            }

            return result;

        } catch (NumberFormatException e) {
            result.setSuccess(false);
            result.setMessage("用户ID或文件ID格式无效");
            result.setFileId(fileId);
            log.error("ID格式错误 - 用户ID: {}, 文件ID: {}", userId, fileId, e);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("文件保存失败: " + e.getMessage());
            result.setFileId(fileId);
            log.error("文件保存异常 - 用户ID: {}, 文件ID: {}", userId, fileId, e);
            return result;
        }
    }

    /**
     * 处理OCR识别
     */
    public OcrResult processOcr(Object fileId) {
        OcrResult result = new OcrResult();

        try {
            Long fileIdLong = Long.parseLong(fileId.toString());
            log.info("开始OCR处理 - 文件ID: {}", fileId);

            // 获取文档
            MedicalDocument document = fileService.getDocumentById(fileIdLong);

            if (document == null) {
                result.setSuccess(false);
                result.setMessage("文件不存在");
                result.setFileId(fileId.toString());
                log.warn("OCR处理失败：文件不存在 - 文件ID: {}", fileId);
                return result;
            }

            // 检查是否已经处理过OCR
            if (document.getOcrResult() != null && !document.getOcrResult().trim().isEmpty()) {
                result.setSuccess(true);
                result.setMessage("OCR已处理（使用缓存结果）");
                result.setFileId(fileId.toString());
                result.setText(document.getOcrResult());
                result.setConfidence(1.0); // 缓存结果默认置信度为1.0
                log.info("返回已缓存的OCR结果 - 文件ID: {}", fileId);
                return result;
            }

            // 检查文件类型是否支持OCR
            String fileType = document.getDocumentType();
            if (!isSupportedOcrFileType(fileType)) {
                result.setSuccess(false);
                result.setMessage("不支持的文件类型: " + fileType);
                result.setFileId(fileId.toString());
                log.warn("不支持的OCR文件类型 - 文件ID: {}, 类型: {}", fileId, fileType);
                return result;
            }

            // 执行OCR处理
            String filePath = document.getFilePath();
            String ocrText = performOcrProcessing(filePath);

            if (ocrText != null && !ocrText.trim().isEmpty()) {
                // 保存OCR结果
                document.setOcrResult(ocrText);
                document.setUpdateTime(LocalDateTime.now());
                fileService.updateDocument(document);

                result.setSuccess(true);
                result.setMessage("OCR处理成功");
                result.setFileId(fileId.toString());
                result.setText(ocrText);
                result.setConfidence(0.95); // 默认置信度
                log.info("OCR处理成功 - 文件ID: {}, 识别文本长度: {}", fileId, ocrText.length());
            } else {
                result.setSuccess(false);
                result.setMessage("OCR处理失败：未识别到文本");
                result.setFileId(fileId.toString());
                log.warn("OCR未识别到文本 - 文件ID: {}", fileId);
            }

            return result;

        } catch (NumberFormatException e) {
            result.setSuccess(false);
            result.setMessage("无效的文件ID格式");
            result.setFileId(fileId.toString());
            log.error("文件ID格式错误 - 文件ID: {}", fileId, e);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("OCR处理失败: " + e.getMessage());
            result.setFileId(fileId.toString());
            log.error("OCR处理异常 - 文件ID: {}", fileId, e);
            return result;
        }
    }

    /**
     * 保存文件（void方法，用于兼容性）
     */
    public void saveFile(String userId, String fileId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            Long fileIdLong = Long.parseLong(fileId);

            log.info("保存文件（void方法）- 用户ID: {}, 文件ID: {}", userId, fileId);

            // 获取文档
            MedicalDocument document = fileService.getDocumentById(fileIdLong);

            if (document != null && document.getUserId().equals(userIdLong)) {
                // 更新文档的最后访问时间
                document.setUpdateTime(LocalDateTime.now());
                fileService.updateDocument(document);
                log.info("文件保存成功（void方法）- 文件ID: {}", fileId);
            } else {
                log.warn("文件保存失败：文件不存在或权限不足 - 用户ID: {}, 文件ID: {}", userId, fileId);
            }
        } catch (NumberFormatException e) {
            log.error("ID格式错误（void方法）- 用户ID: {}, 文件ID: {}", userId, fileId, e);
        } catch (Exception e) {
            log.error("文件保存异常（void方法）- 用户ID: {}, 文件ID: {}", userId, fileId, e);
        }
    }

    /**
     * 上传新文件
     */
    public FileUploadResult saveFile(String userId, MultipartFile file) {
        FileUploadResult result = new FileUploadResult();

        try {
            Long userIdLong = Long.parseLong(userId);

            // 验证文件
            if (file == null || file.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("文件为空");
                log.warn("文件上传失败：文件为空 - 用户ID: {}", userId);
                return result;
            }

            // 验证文件大小（例如：限制50MB）
            long maxSize = 50 * 1024 * 1024; // 50MB
            if (file.getSize() > maxSize) {
                result.setSuccess(false);
                result.setMessage("文件大小超过限制（最大50MB）");
                log.warn("文件上传失败：文件过大 - 用户ID: {}, 文件大小: {} bytes", userId, file.getSize());
                return result;
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                result.setSuccess(false);
                result.setMessage("不支持的文件类型: " + contentType);
                log.warn("文件上传失败：不支持的文件类型 - 用户ID: {}, 类型: {}", userId, contentType);
                return result;
            }

            log.info("开始上传文件 - 用户ID: {}, 文件名: {}, 大小: {} bytes",
                    userId, file.getOriginalFilename(), file.getSize());

            // 上传文档
            MedicalDocument document = fileService.uploadDocument(
                    file,
                    userIdLong,
                    determineDocumentCategory(contentType),
                    "通过控制器上传");

            result.setSuccess(true);
            result.setMessage("文件上传成功");
            result.setFileId(document.getId().toString());
            result.setFileName(document.getDocumentName());

            log.info("文件上传成功 - 用户ID: {}, 文件ID: {}, 文件名: {}",
                    userId, document.getId(), document.getDocumentName());

            return result;

        } catch (NumberFormatException e) {
            result.setSuccess(false);
            result.setMessage("用户ID格式无效");
            log.error("用户ID格式错误 - 用户ID: {}", userId, e);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("文件上传失败: " + e.getMessage());
            log.error("文件上传异常 - 用户ID: {}", userId, e);
            return result;
        }
    }

    /**
     * 转换为DTO
     */
    private MedicalFileDTO convertToDTO(MedicalDocument document) {
        MedicalFileDTO dto = new MedicalFileDTO();
        dto.setId(document.getId().toString());
        dto.setFileName(document.getDocumentName());
        dto.setFileType(document.getDocumentType());
        dto.setFileSize(document.getFileSize());
        dto.setFileUrl(document.getFilePath());

        // 转换上传时间
        if (document.getUploadDate() != null) {
            dto.setUploadTime(new java.sql.Date(
                    document.getUploadDate()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()));
        }

        // 设置状态
        if (document.getOcrResult() != null && !document.getOcrResult().trim().isEmpty()) {
            dto.setStatus("processed");
        } else {
            dto.setStatus("uploaded");
        }

        dto.setOcrResult(document.getOcrResult());

        return dto;
    }

    /**
     * 执行OCR处理
     */
    private String performOcrProcessing(String filePath) {
        try {
            // 使用OCR工具类处理
            // 注意：OCRUtil.extractTextFromImage() 是静态方法，需要传入 MultipartFile 参数
            // 但这里只有文件路径，需要根据实际实现来处理
            // 如果 OCRUtil 支持从文件路径读取，可以使用：
            // return OCRUtil.extractTextFromImage(new File(filePath));
            // 或者根据 OCRUtil 的实际实现来调用相应的方法

            // 临时返回空字符串，需要根据 OCRUtil 的实际实现来修改
            return "";
        } catch (Exception e) {
            log.error("OCR处理失败 - 文件路径: {}", filePath, e);
            return null;
        }
    }

    /**
     * 检查文件类型是否支持OCR
     */
    private boolean isSupportedOcrFileType(String fileType) {
        if (fileType == null) {
            return false;
        }

        String lowerType = fileType.toLowerCase();
        return lowerType.contains("image") ||
                lowerType.contains("jpg") ||
                lowerType.contains("jpeg") ||
                lowerType.contains("png") ||
                lowerType.contains("bmp") ||
                lowerType.contains("gif") ||
                lowerType.contains("pdf");
    }

    /**
     * 验证文件类型是否有效
     */
    private boolean isValidFileType(String contentType) {
        if (contentType == null) {
            return false;
        }

        String lowerType = contentType.toLowerCase();
        return lowerType.startsWith("image/") ||
                lowerType.equals("application/pdf") ||
                lowerType.equals("application/msword") ||
                lowerType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    /**
     * 根据内容类型确定文档分类
     */
    private String determineDocumentCategory(String contentType) {
        if (contentType == null) {
            return "其他";
        }

        String lowerType = contentType.toLowerCase();
        if (lowerType.startsWith("image/")) {
            return "影像资料";
        } else if (lowerType.contains("pdf")) {
            return "检查报告";
        } else if (lowerType.contains("word") || lowerType.contains("document")) {
            return "病历文档";
        } else {
            return "其他";
        }
    }
}