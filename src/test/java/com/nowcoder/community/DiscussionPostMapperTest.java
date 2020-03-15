package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.entity.DiscussPost;
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
public class DiscussionPostMapperTest {

    @Autowired
    DiscussionPostMapper discussionPostMapper;

    @Test
    public void testInsert(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setContent("dfkfhkd大保健");
        discussPost.setTitle("测试");
        discussPost.setCommentCount(22);
        discussPost.setCreateTime(new Date());
        discussPost.setStatus(1);
        discussPost.setUserId(110);
        discussionPostMapper.insertDiscussPost(discussPost);


    }
}
