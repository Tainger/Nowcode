package com.nowcoder.community;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MessageMapperTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testMessageMapper(){
//        List<Message> list =messageMapper.getSessionNewestMessageList(111,0,10);
//        System.out.println(list);
//
//        int count = messageMapper.getSessionCount(111);
//        System.out.println(count);
//
//        int count1 =messageMapper.getMessageCount("111_112");
//        System.out.println(count1);
//
//        List<Message> list1=messageMapper.getMessageList(null,0,100);
//        System.out.println(list1);
//
//        int count2 = messageMapper.getMessageUnReadCount(111,"111_112");
//        System.out.println(count2);

        Message message = new Message();
        message.setContent("56465464");
        message.setToId(45);
        message.setCreateTime(new Date());
        message.setFromId(1);
        message.setStatus(1);
        message.setConversationId("f1d5f145df");
        messageMapper.insertMessage(message);
    }

}
