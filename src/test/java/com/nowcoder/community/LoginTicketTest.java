package com.nowcoder.community;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoginTicketTest {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testAll(){
        int res = loginTicketMapper.updateStatus("123456",1);
        System.out.println(res);
    }
}
