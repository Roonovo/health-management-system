package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取用户增长统计数据
     * 
     * @param days 统计天数
     * @return 每日新增用户统计
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as count " +
            "FROM user " +
            "WHERE create_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY date ASC")
    List<Map<String, Object>> getUserGrowthStatistics(Integer days);

    /**
     * 根据状态统计用户数
     */
    @Select("SELECT status, COUNT(*) as count FROM user GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 统计活跃用户数（最近30天内登录过的）
     */
    @Select("SELECT COUNT(DISTINCT id) FROM user " +
            "WHERE last_login_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)")
    Long countActiveUsers();
}