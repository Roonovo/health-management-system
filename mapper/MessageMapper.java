// mapper/MessageMapper.java
package com.health.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Message;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MessageMapper {

        @Select("SELECT * FROM messages WHERE conversation_id = #{conversationId} ORDER BY create_time ASC")
        List<Message> findByConversationIdOrderByCreateTimeAsc(Long conversationId);

        @Select("SELECT * FROM messages WHERE conversation_id = #{conversationId} " +
                        "ORDER BY create_time DESC LIMIT #{limit}")
        List<Message> findRecentByConversationId(@Param("conversationId") Long conversationId,
                        @Param("limit") int limit);

        @Select("SELECT * FROM messages WHERE id = #{id}")
        Message findById(Long id);

        @Insert("INSERT INTO messages (conversation_id, role, content, health_advice, " +
                        "advice_reason, recipe, liked, create_time) " +
                        "VALUES (#{conversationId}, #{role}, #{content}, #{healthAdvice}, " +
                        "#{adviceReason}, #{recipe}, #{liked}, #{createTime})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        void insert(Message message);

        @Update("UPDATE messages SET liked = #{liked} WHERE id = #{id}")
        void update(Message message);

        @Delete("DELETE FROM messages WHERE conversation_id = #{conversationId}")
        List<Message> deleteByConversationId(Long conversationId);

        @Delete("DELETE FROM messages WHERE conversation_id = #{conversationId} " +
                        "AND create_time > #{createTime}")
        void deleteByConversationIdAndCreateTimeAfter(@Param("conversationId") Long conversationId,
                        @Param("createTime") LocalDateTime createTime);

        Message selectById1(QueryWrapper<Message> queryWrapper);

        Message selectById(QueryWrapper<Message> queryWrapper);

        Message selectById(Long messageId);

        void delete(QueryWrapper<Message> messageQueryWrapper);
}