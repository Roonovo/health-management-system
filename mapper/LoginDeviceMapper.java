package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.LoginDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LoginDeviceMapper extends BaseMapper<LoginDevice> {

    @Select("SELECT * FROM login_device WHERE user_id = #{userId} ORDER BY last_login_time DESC")
    List<LoginDevice> findByUserId(Long userId);
}
