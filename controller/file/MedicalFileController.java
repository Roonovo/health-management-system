package com.health.controller.file;

import com.health.common.Result;
import com.health.entity.MedicalDocument;
import com.health.service.file.MedicalFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Api(tags = "医疗文件管理")
@RestController
@RequestMapping("/api/medical/files")
@CrossOrigin
public class MedicalFileController {

    @Autowired
    private MedicalFileService medicalFileService;

    @Data
    public static class UploadRequest {
        private String category;
        private String description;
    }

    @ApiOperation("上传医疗文档")
    @PostMapping("/upload")
    public Result<MedicalDocument> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false, defaultValue = "其他") String category,
            @RequestParam(value = "description", required = false, defaultValue = "") String description,
            HttpServletRequest request) {

        try {
            Long userId = (Long) request.getAttribute("userId");

            MedicalDocument document = medicalFileService.uploadDocument(
                    file,
                    userId,
                    category,
                    description);

            return Result.success(document);

        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取用户所有文档")
    @GetMapping("/list")
    public Result<List<MedicalDocument>> getUserDocuments(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        List<MedicalDocument> documents = medicalFileService.getUserDocuments(userId);

        return Result.success(documents);
    }

    @ApiOperation("按分类获取文档")
    @GetMapping("/category/{category}")
    public Result<List<MedicalDocument>> getDocumentsByCategory(
            @PathVariable String category,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        List<MedicalDocument> documents = medicalFileService.getDocumentsByCategory(userId, category);

        return Result.success(documents);
    }

    @ApiOperation("获取文档详情")
    @GetMapping("/{documentId}")
    public Result<MedicalDocument> getDocumentById(@PathVariable Long documentId) {
        MedicalDocument document = medicalFileService.getDocumentById(documentId);

        if (document != null) {
            return Result.success(document);
        }

        return Result.error("文档不存在");
    }

    @ApiOperation("删除文档")
    @DeleteMapping("/{documentId}")
    public Result<?> deleteDocument(@PathVariable Long documentId) {
        boolean success = medicalFileService.deleteDocument(documentId);

        if (success) {
            return Result.success("文档删除成功");
        }

        return Result.error("删除失败");
    }

    @ApiOperation("下载文档")
    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        try {
            MedicalDocument document = medicalFileService.getDocumentById(documentId);
            if (document == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = medicalFileService.downloadDocument(documentId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", document.getDocumentName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}