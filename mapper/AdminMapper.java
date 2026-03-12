// src/main/java/com/health/mapper/AdminMapper.java
package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    @Select("SELECT * FROM admin WHERE username = #{username} AND status = 1")
    Admin findByUsername(String username);
}
