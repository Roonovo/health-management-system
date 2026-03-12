package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthRecord;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthRecordRepository extends BaseMapper<HealthRecord> {

    Optional<HealthRecord> findByUserId1(Long userId);
    // 继承BaseMapper后自动有 save, delete, findById 等方法

    HealthRecord save(HealthRecord record);

    Optional<HealthRecord> findByUserId(Long userId);

    Optional<HealthRecord> findByUserId(String userId);
}
