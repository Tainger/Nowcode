package com.nowcoder.community;


import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.dao.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussionPostMapper discussionPostMapper;

    @Test
    public void Test() {
        User user = userMapper.selectById(1);
        User user1 = userMapper.selectByEmail("nowcoder1@sina.com");
        User user2 = userMapper.selectByName("SYSTEM");
        System.out.println(user);
        System.out.println(user1);
        System.out.println(user2);

//        userMapper.updateUserHead( "0.0",1);
//        userMapper.updateUserStatus(2,1);
//        userMapper.updateUserPassword( "....",1);

    }

    @Test
    public  void testDiscussionPostMapper(){
        int id = discussionPostMapper.selectDiscussPostRows(101);
        User user = userMapper.selectById(101);
        System.err.println(user);
        List<DiscussPost> list = discussionPostMapper.selectUserPosts(101,1,100);
        System.out.println(id);
        for(DiscussPost it:list){
            System.out.println(it);
        }
    }
}
