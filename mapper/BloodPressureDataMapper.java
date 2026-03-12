package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.health.dto.response.BloodPressureData;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BloodPressureDataMapper extends BaseMapper<HealthRecord> {

    @Select("SELECT id, blood_pressure_systolic as systolic, blood_pressure_diastolic as diastolic, record_date " +
            "FROM health_record " +
            "WHERE user_id = #{userId} " +
            "AND blood_pressure_systolic IS NOT NULL " +
            "AND blood_pressure_diastolic IS NOT NULL " +
            "AND record_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY record_date ASC")
    List<BloodPressureData> getBloodPressureData(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
