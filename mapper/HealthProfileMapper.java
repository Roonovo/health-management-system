package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HealthProfileMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE id = #{userId}")
    User getHealthProfile(Long userId);
}