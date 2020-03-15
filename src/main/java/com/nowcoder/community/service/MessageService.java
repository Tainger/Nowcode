package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public List<Message>  getSessionNewestMessageList(int userId,int offset,int limit){
        return messageMapper.getSessionNewestMessageList(userId,offset,limit);
    }

    public int getSessionCount(int userId){
        return messageMapper.getSessionCount(userId);
    }

    public List<Message> getMessageList(String conversationId,int offset,int limit){

        return messageMapper.getMessageList(conversationId,offset,limit);
    }

    public int getMessageCount(String conversationId){
        return messageMapper.getMessageCount(conversationId);
    }

    public int getMessageUnReadCount( int userId,String conversationId){
        return messageMapper.getMessageUnReadCount(userId,conversationId);
    }

    public void insertMessage(Message message) {
        messageMapper.insertMessage(message);
    }
}
