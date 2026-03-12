package com.health.controller.health;

import java.util.List;

import com.health.entity.HealthRecord;
import com.health.entity.MedicalDocument;

public class Result {

    public static com.health.common.Result<List<MedicalDocument>> success(List<MedicalDocument> documents) {
        return com.health.common.Result.success(documents, "获取医疗文档列表成功");
    }

    public static com.health.common.Result<HealthRecord> success(HealthRecord record, String message) {
        return com.health.common.Result.success(record, message);
    }

}
