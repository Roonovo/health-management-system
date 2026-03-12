// mapper/ConversationMapper.java
package com.health.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Conversation;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ConversationMapper {

        @Select("SELECT * FROM conversations WHERE user_id = #{userId} ORDER BY update_time DESC")
        List<Conversation> findByUserIdOrderByUpdateTimeDesc(Long userId);

        @Select("SELECT COUNT(*) FROM conversations WHERE user_id = #{userId}")
        int countByUserId(Long userId);

        @Select("SELECT * FROM conversations WHERE id = #{id}")
        Conversation findById(Long id);

        @Insert("INSERT INTO conversations (user_id, title, message_count, create_time, update_time) " +
                        "VALUES (#{userId}, #{title}, #{messageCount}, #{createTime}, #{updateTime})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        void insert(Conversation conversation);

        @Update("UPDATE conversations SET title = #{title}, last_message = #{lastMessage}, " +
                        "message_count = #{messageCount}, update_time = #{updateTime} WHERE id = #{id}")
        void update(Conversation conversation);

        @Delete("DELETE FROM conversations WHERE id = #{id}")
        void deleteById(Long id);

        Conversation selectOne(QueryWrapper<Conversation> queryWrapper);
}