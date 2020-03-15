package com.nowcoder.community.dao;


import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户的未读消息的会话列表，返回当前信息的最新列表
    List<Message> getSessionNewestMessageList(@Param("userId") int userId,
                                              @Param("offset")int offset,
                                              @Param("limit")int limit);
    //查询当前用户的会话数量，用于分页
    int  getSessionCount( @Param("userId")int userId);
    //查询某个会话的私信列表
    List<Message> getMessageList( @Param("conversationId")String conversationId,
                                  @Param("offset")int offset,
                                  @Param("limit")int limit);
    //查询某个会话私信数量
    int getMessageCount( @Param("conversationId")String conversationId);
    //查询未读私信的数量
    int getMessageUnReadCount( @Param("userId")int userId,
                          @Param("conversationId")String conversationId);
    //插入某个信息
    void insertMessage(Message message);
}
