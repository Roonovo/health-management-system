package com.health.service.health;

import com.alibaba.fastjson.JSONObject;
import com.health.utils.OCRUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OCR服务实现类
 * 集成百度OCR API进行文字识别
 */
@Service
public class ocrUtil {

    @Autowired
    private OCRUtil ocrUtil;

    /**
     * 识别图片中的文字
     * 
     * @param file 上传的图片文件
     * @return OCR识别结果
     * @throws IOException 文件读取异常
     */
    public String recognizeText(MultipartFile file) throws IOException {
        return ocrUtil.extractTextFromImage(file);
    }

    /**
     * 识别图片中的文字并解析为结构化数据
     * 
     * @param file 上传的图片文件
     * @return 解析后的医疗文档信息
     * @throws IOException 文件读取异常
     */
    public OCRUtil.MedicalDocumentInfo recognizeAndParse(MultipartFile file) throws IOException {
        String ocrText = recognizeText(file);
        return ocrUtil.parseJsonFromArray(ocrText);
    }

    /**
     * 通用OCR识别方法
     * 支持多种OCR识别类型
     * 
     * @param file    上传的图片文件
     * @param ocrType 识别类型（general:通用文字识别, medical:医疗文档识别）
     * @return 识别结果
     * @throws IOException 文件读取异常
     */
    public String recognizeTextWithType(MultipartFile file, String ocrType) throws IOException {
        if ("medical".equals(ocrType)) {
            // 医疗文档专用识别
            return recognizeMedicalDocument(file);
        } else {
            // 通用文字识别
            return recognizeText(file);
        }
    }

    /**
     * 医疗文档专用识别
     * 
     * @param file 医疗文档图片
     * @return 识别结果
     * @throws IOException 文件读取异常
     */
    private String recognizeMedicalDocument(MultipartFile file) throws IOException {
        // 这里可以集成专门的医疗OCR服务
        // 或者使用通用OCR但增加医疗术语识别

        String result = recognizeText(file);

        // 后处理：增强医疗术语识别
        return enhanceMedicalRecognition(result);
    }

    /**
     * 增强医疗术语识别
     * 
     * @param text OCR识别的原始文本
     * @return 增强后的文本
     */
    private String enhanceMedicalRecognition(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // 医疗术语映射表
        Map<String, String> medicalTerms = new HashMap<>();
        medicalTerms.put("阿莫西林", "Amoxicillin");
        medicalTerms.put("头孢", "Cephalosporin");
        medicalTerms.put("布洛芬", "Ibuprofen");
        medicalTerms.put("阿司匹林", "Aspirin");
        medicalTerms.put("胰岛素", "Insulin");
        medicalTerms.put("血压", "Blood Pressure");
        medicalTerms.put("血糖", "Blood Sugar");
        medicalTerms.put("心率", "Heart Rate");

        // 简单的术语替换
        for (Map.Entry<String, String> entry : medicalTerms.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        return text;
    }

    /**
     * 批量OCR识别
     * 
     * @param files 多个图片文件
     * @return 识别结果列表
     * @throws IOException 文件读取异常
     */
    public String[] batchRecognize(MultipartFile[] files) throws IOException {
        String[] results = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            results[i] = recognizeText(files[i]);
        }

        return results;
    }

    /**
     * 获取OCR识别统计信息
     * 
     * @param file 上传的图片文件
     * @return 统计信息
     * @throws IOException 文件读取异常
     */
    public JSONObject getRecognitionStats(MultipartFile file) throws IOException {
        JSONObject stats = new JSONObject();

        try {
            String result = recognizeText(file);
            stats.put("fileName", file.getOriginalFilename());
            stats.put("fileSize", file.getSize());
            stats.put("recognitionTime", System.currentTimeMillis());
            stats.put("textLength", result.length());
            stats.put("success", true);
            stats.put("result", result);
        } catch (Exception e) {
            stats.put("success", false);
            stats.put("error", e.getMessage());
        }

        return stats;
    }
}
