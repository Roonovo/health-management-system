package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.health.dto.response.HeartRateData;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HeartRateDataMapper extends BaseMapper<HealthRecord> {

    @Select("SELECT id, heart_rate, record_date FROM health_record " +
            "WHERE user_id = #{userId} AND heart_rate IS NOT NULL " +
            "AND record_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY record_date ASC")
    List<HeartRateData> getHeartRateData(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
