package com.health.service.file;

import com.health.entity.MedicalDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 医疗文件服务接口
 */
public interface MedicalFileService {

    /**
     * 上传医疗文档
     * 
     * @param file        文件
     * @param userId      用户ID
     * @param category    分类
     * @param description 描述
     * @return 文档信息
     */
    MedicalDocument uploadDocument(MultipartFile file, Long userId, String category, String description)
            throws IOException;

    /**
     * 获取用户的所有医疗文档
     * 
     * @param userId 用户ID
     * @return 文档列表
     */
    List<MedicalDocument> getUserDocuments(Long userId);

    /**
     * 按分类获取文档
     * 
     * @param userId   用户ID
     * @param category 分类
     * @return 文档列表
     */
    List<MedicalDocument> getDocumentsByCategory(Long userId, String category);

    /**
     * 删除文档
     * 
     * @param documentId 文档ID
     * @return 是否成功
     */
    boolean deleteDocument(Long documentId);

    /**
     * 更新医疗文档
     */
    boolean updateDocument(MedicalDocument document);

    /**
     * 获取文档详情
     * 
     * @param documentId 文档ID
     * @return 文档信息
     */
    MedicalDocument getDocumentById(Long documentId);

    /**
     * 下载文档
     * 
     * @param documentId 文档ID
     * @return 文件字节数组
     */
    byte[] downloadDocument(Long documentId) throws IOException;
}
