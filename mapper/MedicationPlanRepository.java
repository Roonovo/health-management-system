package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.MedicationPlan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MedicationPlanRepository extends BaseMapper<MedicationPlan> {

    MedicationPlan save(MedicationPlan plan);
}
