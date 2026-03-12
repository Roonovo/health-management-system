package com.health.service.health;

import com.health.dto.request.HealthDataRequest;
import com.health.dto.response.BloodPressureData;
import com.health.dto.response.BloodSugarData;
import com.health.dto.response.HealthStatDTO;
import com.health.dto.response.HealthStatisticsResponse;
import com.health.dto.response.HeartRateData;
import com.health.dto.response.WarningDTO;
import com.health.dto.response.WeightData;
import com.health.entity.HealthRecord;
import com.health.entity.User;
import com.health.mapper.BloodPressureDataMapper;
import com.health.mapper.BloodSugarDataMapper;
import com.health.mapper.HealthProfileMapper;
import com.health.mapper.HeartRateDataMapper;
import com.health.mapper.WeightDataMapper;
import com.health.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthDataService {

    private final WeightDataMapper weightDataMapper;
    private final BloodPressureDataMapper bloodPressureDataMapper;
    private final BloodSugarDataMapper bloodSugarDataMapper;
    private final HeartRateDataMapper heartRateDataMapper;
    private final HealthProfileMapper healthProfileMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    public List<WeightData> getWeightData(String timeRange, LocalDateTime startDate, LocalDateTime endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDateTime[] dateRange = calculateDateRange(timeRange, startDate, endDate);

        List<WeightData> dataList = weightDataMapper.getWeightData(userId, dateRange[0], dateRange[1]);

        // 格式化日期用于前端展示 - 创建新的对象并设置格式化后的日期
        List<WeightData> formattedDataList = dataList.stream()
                .map(data -> new WeightData(data.getId(), data.getWeight(),
                        LocalDateTime.parse(data.getRecordDate().format(DATE_FORMATTER), DATE_FORMATTER)))
                .collect(Collectors.toList());
        return formattedDataList;
    }

    @Transactional
    public void addWeightData(HealthDataRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 创建HealthRecord对象而不是WeightData
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setUserId(userId);
        healthRecord.setWeight(request.getValue().doubleValue());
        healthRecord.setRecordDate(request.getRecordDate());
        healthRecord.setCreateTime(LocalDateTime.now());
        weightDataMapper.insert(healthRecord);

        // 更新健康档案中的体重信息
        updateHealthProfileWeight(userId, request.getValue());
    }

    public List<BloodPressureData> getBloodPressureData(String timeRange, LocalDateTime startDate,
            LocalDateTime endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDateTime[] dateRange = calculateDateRange(timeRange, startDate, endDate);

        List<BloodPressureData> dataList = bloodPressureDataMapper.getBloodPressureData(
                userId, dateRange[0], dateRange[1]);

        // 格式化日期用于前端展示 - 创建新的对象并设置格式化后的日期
        List<BloodPressureData> formattedDataList = dataList.stream()
                .map(data -> new BloodPressureData(data.getId(), data.getSystolic(), data.getDiastolic(),
                        LocalDateTime.parse(data.getRecordDate().format(DATE_FORMATTER), DATE_FORMATTER)))
                .collect(Collectors.toList());
        return formattedDataList;
    }

    @Transactional
    public void addBloodPressureData(HealthDataRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 创建HealthRecord对象而不是BloodPressureData
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setUserId(userId);
        healthRecord.setBloodPressureSystolic(request.getSystolic());
        healthRecord.setBloodPressureDiastolic(request.getDiastolic());
        healthRecord.setRecordDate(request.getRecordDate());
        healthRecord.setCreateTime(LocalDateTime.now());
        bloodPressureDataMapper.insert(healthRecord);
    }

    public List<BloodSugarData> getBloodSugarData(String timeRange, LocalDateTime startDate, LocalDateTime endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDateTime[] dateRange = calculateDateRange(timeRange, startDate, endDate);

        List<BloodSugarData> dataList = bloodSugarDataMapper.getBloodSugarData(
                userId, dateRange[0], dateRange[1]);

        // 格式化日期用于前端展示 - 创建新的对象并设置格式化后的日期
        List<BloodSugarData> formattedDataList = dataList.stream()
                .map(data -> new BloodSugarData(data.getId(), data.getBloodSugar(),
                        LocalDateTime.parse(data.getRecordDate().format(DATE_FORMATTER), DATE_FORMATTER)))
                .collect(Collectors.toList());
        return formattedDataList;
    }

    @Transactional
    public void addBloodSugarData(HealthDataRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 创建HealthRecord对象而不是BloodSugarData
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setUserId(userId);
        healthRecord.setBloodSugar(request.getValue().doubleValue());
        healthRecord.setRecordDate(request.getRecordDate());
        healthRecord.setCreateTime(LocalDateTime.now());
        bloodSugarDataMapper.insert(healthRecord);
    }

    public List<HeartRateData> getHeartRateData(String timeRange, LocalDateTime startDate, LocalDateTime endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDateTime[] dateRange = calculateDateRange(timeRange, startDate, endDate);

        List<HeartRateData> dataList = heartRateDataMapper.getHeartRateData(
                userId, dateRange[0], dateRange[1]);

        // 格式化日期用于前端展示 - 创建新的对象并设置格式化后的日期
        List<HeartRateData> formattedDataList = dataList.stream()
                .map(data -> new HeartRateData(data.getId(), data.getHeartRate(),
                        LocalDateTime.parse(data.getRecordDate().format(DATE_FORMATTER), DATE_FORMATTER)))
                .collect(Collectors.toList());
        return formattedDataList;
    }

    @Transactional
    public void addHeartRateData(HealthDataRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 创建HealthRecord对象而不是HeartRateData
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setUserId(userId);
        healthRecord.setHeartRate(request.getValue().intValue());
        healthRecord.setRecordDate(request.getRecordDate());
        healthRecord.setCreateTime(LocalDateTime.now());
        heartRateDataMapper.insert(healthRecord);
    }

    public HealthStatisticsResponse getHealthStatistics(String timeRange, LocalDateTime startDate,
            LocalDateTime endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDateTime[] dateRange = calculateDateRange(timeRange, startDate, endDate);

        HealthStatisticsResponse response = new HealthStatisticsResponse();
        List<HealthStatDTO> stats = new ArrayList<>();
        List<WarningDTO> warnings = new ArrayList<>();

        // 获取健康档案中的目标值
        User profile = healthProfileMapper.getHealthProfile(userId);
        // 体重统计
        HealthStatDTO weightStat = calculateWeightStatistics(userId, dateRange, profile);
        stats.add(weightStat);
        if (weightStat.getStatus().equals("warning") || weightStat.getStatus().equals("danger")) {
            warnings.add(createWarning("体重", weightStat));
        }

        // 血压统计
        HealthStatDTO bpStat = calculateBloodPressureStatistics(userId, dateRange);
        stats.add(bpStat);
        if (bpStat.getStatus().equals("warning") || bpStat.getStatus().equals("danger")) {
            warnings.add(createWarning("血压", bpStat));
        }

        // 血糖统计
        HealthStatDTO bsStat = calculateBloodSugarStatistics(userId, dateRange);
        stats.add(bsStat);
        if (bsStat.getStatus().equals("warning") || bsStat.getStatus().equals("danger")) {
            warnings.add(createWarning("血糖", bsStat));
        }

        // 心率统计
        HealthStatDTO hrStat = calculateHeartRateStatistics(userId, dateRange);
        stats.add(hrStat);
        if (hrStat.getStatus().equals("warning") || hrStat.getStatus().equals("danger")) {
            warnings.add(createWarning("心率", hrStat));
        }

        response.setStats(stats);
        response.setWarnings(warnings);

        return response;
    }

    private HealthStatDTO calculateWeightStatistics(Long userId, LocalDateTime[] dateRange, User profile) {
        List<WeightData> dataList = weightDataMapper.getWeightData(
                userId, dateRange[0], dateRange[1]);

        HealthStatDTO stat = new HealthStatDTO();
        stat.setType("weight");
        stat.setName("体重");
        stat.setIcon("el-icon-scale");
        stat.setUnit("kg");

        if (!dataList.isEmpty()) {
            WeightData latest = dataList.get(dataList.size() - 1);
            stat.setCurrent(String.valueOf(latest.getWeight()));
            // 计算平均值
            Double average = dataList.stream()
                    .map(WeightData::getWeight)
                    .reduce(0.0, Double::sum) / dataList.size();
            stat.setAverage(String.valueOf(average));

            // 设置目标值 - 使用身高计算理想体重范围
            if (profile != null && profile.getHeight() != null) {
                Double idealWeight = (profile.getHeight() - 100) * 0.9;
                stat.setGoal(String.valueOf(Math.round(idealWeight)));
            }
            // 计算趋势
            if (dataList.size() >= 2) {
                Double previousAvg = calculatePreviousPeriodAverage(dataList);
                Double change = average - previousAvg;

                if (Math.abs(change) < 0.5) {
                    stat.setTrendType("stable");
                    stat.setTrendIcon("el-icon-minus");
                    stat.setTrendText("保持稳定");
                    stat.setStatus("normal");
                } else if (change < 0) {
                    stat.setTrendType("down");
                    stat.setTrendIcon("el-icon-bottom");
                    stat.setTrendText("较上周下降" + Math.abs(change) + "kg");
                    stat.setStatus("normal");
                } else {
                    stat.setTrendType("up");
                    stat.setTrendIcon("el-icon-top");
                    stat.setTrendText("较上周上升" + change + "kg");
                    stat.setStatus("warning");
                }
            }
            // 判断是否异常
            if (profile != null && profile.getHeight() != null) {
                Double bmi = calculateBMI(latest.getWeight(), profile.getHeight());
                if (bmi > 24.0 || bmi < 18.5) {
                    stat.setStatus("warning");
                }
            }
        }

        return stat;
    }

    private HealthStatDTO calculateBloodPressureStatistics(Long userId, LocalDateTime[] dateRange) {
        List<BloodPressureData> dataList = bloodPressureDataMapper.getBloodPressureData(
                userId, dateRange[0], dateRange[1]);

        HealthStatDTO stat = new HealthStatDTO();
        stat.setType("bloodPressure");
        stat.setName("血压");
        stat.setIcon("el-icon-heart");
        stat.setUnit("mmHg");

        if (!dataList.isEmpty()) {
            BloodPressureData latest = dataList.get(dataList.size() - 1);
            stat.setCurrent(latest.getSystolic() + "/" + latest.getDiastolic());

            // 计算平均值
            int avgSystolic = (int) dataList.stream()
                    .mapToInt(BloodPressureData::getSystolic)
                    .average()
                    .orElse(0);
            int avgDiastolic = (int) dataList.stream()
                    .mapToInt(BloodPressureData::getDiastolic)
                    .average()
                    .orElse(0);
            stat.setAverage(avgSystolic + "/" + avgDiastolic);
            stat.setGoal("120/80");

            // 判断状态
            if (latest.getSystolic() >= 140 || latest.getDiastolic() >= 90) {
                stat.setStatus("danger");
                stat.setTrendType("up");
                stat.setTrendIcon("el-icon-top");
                stat.setTrendText("血压偏高，请注意");
            } else if (latest.getSystolic() >= 130 || latest.getDiastolic() >= 85) {
                stat.setStatus("warning");
                stat.setTrendType("stable");
                stat.setTrendIcon("el-icon-minus");
                stat.setTrendText("血压略高，需关注");
            } else {
                stat.setStatus("normal");
                stat.setTrendType("stable");
                stat.setTrendIcon("el-icon-minus");
                stat.setTrendText("保持稳定");
            }
        }

        return stat;
    }

    private HealthStatDTO calculateBloodSugarStatistics(Long userId, LocalDateTime[] dateRange) {
        List<BloodSugarData> dataList = bloodSugarDataMapper.getBloodSugarData(
                userId, dateRange[0], dateRange[1]);

        HealthStatDTO stat = new HealthStatDTO();
        stat.setType("bloodSugar");
        stat.setName("血糖");
        stat.setIcon("el-icon-sugar");
        stat.setUnit("mmol/L");

        if (!dataList.isEmpty()) {
            BloodSugarData latest = dataList.get(dataList.size() - 1);
            stat.setCurrent(String.valueOf(latest.getBloodSugar()));

            // 计算平均值
            Double average = dataList.stream()
                    .map(BloodSugarData::getBloodSugar)
                    .reduce(0.0, Double::sum) / dataList.size();
            stat.setAverage(String.valueOf(average));
            stat.setGoal("5.5");

            // 判断状态
            if (latest.getBloodSugar() > 7.0) {
                stat.setStatus("danger");
                stat.setTrendType("up");
                stat.setTrendIcon("el-icon-top");
                stat.setTrendText("血糖偏高，请注意");
            } else if (latest.getBloodSugar() > 6.1) {
                stat.setStatus("warning");
                stat.setTrendType("stable");
                stat.setTrendIcon("el-icon-minus");
                stat.setTrendText("血糖略高，需关注");
            } else {
                stat.setStatus("normal");
                stat.setTrendType("down");
                stat.setTrendIcon("el-icon-bottom");
                stat.setTrendText("保持良好");
            }
        }

        return stat;
    }

    private HealthStatDTO calculateHeartRateStatistics(Long userId, LocalDateTime[] dateRange) {
        List<HeartRateData> dataList = heartRateDataMapper.getHeartRateData(
                userId, dateRange[0], dateRange[1]);

        HealthStatDTO stat = new HealthStatDTO();
        stat.setType("heartRate");
        stat.setName("心率");
        stat.setIcon("el-icon-data-line");
        stat.setUnit("bpm");

        if (!dataList.isEmpty()) {
            HeartRateData latest = dataList.get(dataList.size() - 1);
            stat.setCurrent(String.valueOf(latest.getHeartRate()));

            // 计算平均值
            int average = (int) dataList.stream()
                    .mapToInt(HeartRateData::getHeartRate)
                    .average()
                    .orElse(0);
            stat.setAverage(String.valueOf(average));
            stat.setGoal("70");

            // 判断状态
            if (latest.getHeartRate() > 100 || latest.getHeartRate() < 60) {
                stat.setStatus("warning");
                stat.setTrendType("stable");
                stat.setTrendIcon("el-icon-minus");
                stat.setTrendText("心率异常，需关注");
            } else {
                stat.setStatus("normal");
                stat.setTrendType("stable");
                stat.setTrendIcon("el-icon-minus");
                stat.setTrendText("保持稳定");
            }
        }

        return stat;
    }

    private LocalDateTime[] calculateDateRange(String timeRange, LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start;

        if (startDate != null && endDate != null) {
            return new LocalDateTime[] { startDate, endDate };
        }

        switch (timeRange) {
            case "day":
                start = end.minusDays(1);
                break;
            case "week":
                start = end.minusWeeks(1);
                break;
            case "year":
                start = end.minusYears(1);
                break;
            case "month":
            default:
                start = end.minusMonths(1);
                break;
        }

        return new LocalDateTime[] { start, end };
    }

    private Double calculatePreviousPeriodAverage(List<WeightData> dataList) {
        int halfSize = dataList.size() / 2;
        return dataList.subList(0, halfSize).stream()
                .map(WeightData::getWeight)
                .reduce(0.0, Double::sum) / halfSize;
    }

    private Double calculateBMI(Double weight, Double height) {
        Double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }

    private void updateHealthProfileWeight(Long userId, BigDecimal weight) {
        User profile = healthProfileMapper.getHealthProfile(userId);
        if (profile != null) {
            profile.setWeight(weight.doubleValue());
            profile.setUpdateTime(LocalDateTime.now());
            healthProfileMapper.updateById(profile);
        }
    }

    private WarningDTO createWarning(String type, HealthStatDTO stat) {
        WarningDTO warning = new WarningDTO();
        warning.setId(System.currentTimeMillis());
        warning.setTitle(type + "异常提醒");
        warning.setType(stat.getStatus().equals("danger") ? "error" : "warning");
        warning.setDescription("您的" + type + "指标" + stat.getCurrent() + stat.getUnit() +
                "，" + stat.getTrendText() + "，建议及时关注并咨询医生。");
        return warning;
    }
}