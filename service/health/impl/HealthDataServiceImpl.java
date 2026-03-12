package com.health.service.health.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.dto.response.*;
import com.health.entity.HealthRecord;
import com.health.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthDataServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> {

        @Autowired(required = false)
        private WeightDataMapper weightDataMapper;

        @Autowired(required = false)
        private BloodPressureDataMapper bloodPressureDataMapper;

        @Autowired(required = false)
        private BloodSugarDataMapper bloodSugarDataMapper;

        @Autowired(required = false)
        private HeartRateDataMapper heartRateDataMapper;

        public List<WeightData> getWeightData(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
                // 如果有专门的Mapper，使用Mapper
                if (weightDataMapper != null) {
                        return weightDataMapper.getWeightData(userId, startDate, endDate);
                }

                // 否则使用通用查询
                QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId)
                                .isNotNull("weight")
                                .between("record_date", startDate, endDate)
                                .orderByAsc("record_date");

                List<HealthRecord> records = list(queryWrapper);

                return records.stream()
                                .map(record -> new WeightData(
                                                record.getId(),
                                                record.getWeight(),
                                                record.getRecordDate()))
                                .collect(Collectors.toList());
        }

        public List<BloodPressureData> getBloodPressureData(Long userId, LocalDateTime startDate,
                        LocalDateTime endDate) {
                if (bloodPressureDataMapper != null) {
                        return bloodPressureDataMapper.getBloodPressureData(userId, startDate, endDate);
                }

                QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId)
                                .isNotNull("blood_pressure_systolic")
                                .isNotNull("blood_pressure_diastolic")
                                .between("record_date", startDate, endDate)
                                .orderByAsc("record_date");

                List<HealthRecord> records = list(queryWrapper);

                return records.stream()
                                .map(record -> new BloodPressureData(
                                                record.getId(),
                                                record.getBloodPressureSystolic(),
                                                record.getBloodPressureDiastolic(),
                                                record.getRecordDate()))
                                .collect(Collectors.toList());
        }

        public List<BloodSugarData> getBloodSugarData(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
                if (bloodSugarDataMapper != null) {
                        return bloodSugarDataMapper.getBloodSugarData(userId, startDate, endDate);
                }

                QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId)
                                .isNotNull("blood_sugar")
                                .between("record_date", startDate, endDate)
                                .orderByAsc("record_date");

                List<HealthRecord> records = list(queryWrapper);

                return records.stream()
                                .map(record -> new BloodSugarData(
                                                record.getId(),
                                                record.getBloodSugar(),
                                                record.getRecordDate()))
                                .collect(Collectors.toList());
        }

        public List<HeartRateData> getHeartRateData(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
                if (heartRateDataMapper != null) {
                        return heartRateDataMapper.getHeartRateData(userId, startDate, endDate);
                }

                QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId)
                                .isNotNull("heart_rate")
                                .between("record_date", startDate, endDate)
                                .orderByAsc("record_date");

                List<HealthRecord> records = list(queryWrapper);

                return records.stream()
                                .map(record -> new HeartRateData(
                                                record.getId(),
                                                record.getHeartRate(),
                                                record.getRecordDate()))
                                .collect(Collectors.toList());
        }
}