package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.MedicalDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicalDocumentMapper extends BaseMapper<MedicalDocument> {

    @Select("SELECT * FROM medical_document WHERE user_id = #{userId} ORDER BY upload_date DESC")
    List<MedicalDocument> findByUserId(Long userId);

    @Select("SELECT * FROM medical_document WHERE user_id = #{userId} AND category = #{category} ORDER BY upload_date DESC")
    List<MedicalDocument> findByUserIdAndCategory(Long userId, String category);
}