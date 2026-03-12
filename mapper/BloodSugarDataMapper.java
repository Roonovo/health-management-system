package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.health.dto.response.BloodSugarData;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BloodSugarDataMapper extends BaseMapper<HealthRecord> {

    @Select("SELECT id, blood_sugar, record_date FROM health_record " +
            "WHERE user_id = #{userId} AND blood_sugar IS NOT NULL " +
            "AND record_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY record_date ASC")
    List<BloodSugarData> getBloodSugarData(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}