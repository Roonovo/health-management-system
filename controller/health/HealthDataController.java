// HealthDataController.java
package com.health.controller.health;

import com.health.dto.request.HealthDataRequest;
import com.health.dto.response.BloodPressureData;
import com.health.dto.response.BloodSugarData;
import com.health.dto.response.HealthStatisticsResponse;
import com.health.dto.response.HeartRateData;
import com.health.dto.response.WeightData;
import com.health.service.health.HealthDataService;
import com.health.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "健康数据管理")
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthDataController {

    private final HealthDataService healthDataService;

    @ApiOperation("获取体重数据")
    @GetMapping("/weight")
    public Result<List<WeightData>> getWeightData(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        List<WeightData> data = healthDataService.getWeightData(timeRange, startDate, endDate);
        return Result.success(data);
    }

    @ApiOperation("添加体重数据")
    @PostMapping("/weight")
    public Result<Void> addWeightData(@Valid @RequestBody HealthDataRequest request) {
        healthDataService.addWeightData(request);
        return Result.success(null);
    }

    @ApiOperation("获取血压数据")
    @GetMapping("/blood-pressure")
    public Result<List<BloodPressureData>> getBloodPressureData(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        List<BloodPressureData> data = healthDataService.getBloodPressureData(timeRange, startDate, endDate);
        return Result.success(data);
    }

    @ApiOperation("添加血压数据")
    @PostMapping("/blood-pressure")
    public Result<Void> addBloodPressureData(@Valid @RequestBody HealthDataRequest request) {
        healthDataService.addBloodPressureData(request);
        return Result.success(null);
    }

    @ApiOperation("获取血糖数据")
    @GetMapping("/blood-sugar")
    public Result<List<BloodSugarData>> getBloodSugarData(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        List<BloodSugarData> data = healthDataService.getBloodSugarData(timeRange, startDate, endDate);
        return Result.success(data);
    }

    @ApiOperation("添加血糖数据")
    @PostMapping("/blood-sugar")
    public Result<Void> addBloodSugarData(@Valid @RequestBody HealthDataRequest request) {
        healthDataService.addBloodSugarData(request);
        return Result.success(null);
    }

    @ApiOperation("获取心率数据")
    @GetMapping("/heart-rate")
    public Result<List<HeartRateData>> getHeartRateData(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        List<HeartRateData> data = healthDataService.getHeartRateData(timeRange, startDate, endDate);
        return Result.success(data);
    }

    @ApiOperation("添加心率数据")
    @PostMapping("/heart-rate")
    public Result<Void> addHeartRateData(@Valid @RequestBody HealthDataRequest request) {
        healthDataService.addHeartRateData(request);
        return Result.success(null);
    }

    @ApiOperation("获取健康统计数据")
    @GetMapping("/statistics")
    public Result<HealthStatisticsResponse> getHealthStatistics(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        HealthStatisticsResponse statistics = healthDataService.getHealthStatistics(timeRange, startDate, endDate);
        return Result.success(statistics);
    }
}