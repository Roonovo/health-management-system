package com.health.controller.health;

import com.health.entity.HealthRecord;
import com.health.entity.MedicalDocument;
import com.health.service.health.HealthRecordDTO;
import com.health.service.health.HealthRecordService;
import com.health.utils.JwtUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import com.health.common.Result;

@RestController
@RequestMapping("/api/health-records")
@Api(tags = "健康档案接口")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 创建健康档案
     */
    @PostMapping
    @ApiOperation("创建健康档案")
    public Result<HealthRecord> createRecord(@Validated @RequestBody HealthRecordDTO dto,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        HealthRecord record = healthRecordService.createRecord(Long.valueOf(userId), dto);
        return Result.success(record, "档案创建成功");
    }

    /**
     * 更新健康档案
     */
    @PutMapping
    @ApiOperation("更新健康档案")
    public Result<HealthRecord> updateRecord(@RequestBody HealthRecordDTO dto,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        HealthRecord record = healthRecordService.updateRecord(Long.valueOf(userId), dto);
        return Result.success(record, "档案更新成功");
    }

    /**
     * 获取健康档案
     */
    @GetMapping
    @ApiOperation("获取健康档案")
    public Result<HealthRecord> getRecord(HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        HealthRecord record = healthRecordService.getRecordByUserId(Long.parseLong(userId));
        return Result.success(record);
    }

    /**
     * 上传医疗文档
     */
    @PostMapping("/documents")
    @ApiOperation("上传医疗文档")
    public Result<MedicalDocument> uploadDocument(@RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        MedicalDocument document = healthRecordService.uploadDocument(Long.parseLong(userId), file);
        return Result.success(document, "文档上传成功");
    }

    /**
     * 获取医疗文档列表
     */
    @GetMapping("/documents")
    @ApiOperation("获取医疗文档列表")
    public Result<List<MedicalDocument>> getDocuments(HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        Optional<List<MedicalDocument>> documents = healthRecordService.getDocumentsByUserId(Long.parseLong(userId));
        return Result.success(documents.orElse(null));
    }

    /**
     * 删除医疗文档
     */
    @DeleteMapping("/documents/{documentId}")
    @ApiOperation("删除医疗文档")
    public Result<Void> deleteDocument(@PathVariable Long documentId,
            HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        healthRecordService.deleteDocument(Long.parseLong(userId), documentId);
        return Result.success();
    }

    /**
     * 从请求中获取用户ID
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}