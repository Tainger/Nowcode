package com.nowcoder.community;


import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.util.CommunityConstant;
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
public class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussionPostMapper discussionPostMapper;

    @Test
    public void test(){
        List<Comment> comments = commentMapper.selectComments(CommunityConstant.ENTITY_TYPE_POST,236,0,1000);
        for(Comment comment:comments){
            System.err.println("原评论"+comment);
            List<Comment> replyList= commentMapper.selectComments(CommunityConstant.ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
            for(Comment comment1:replyList){
                System.err.println("评论回复"+comment1);
            }
        }
    }

    @Test
    public void testCommentAndIncreCommentCount(){
        Comment comment = new Comment();
        comment.setEntityType(1);
        comment.setEntityId(275);
        comment.setCreateTime(new Date());
        comment.setUserId(6666683);
        comment.setContent("564564654645645646546465");
        comment.setStatus(0);
        commentMapper.addComment(comment);
        DiscussPost discussPost =discussionPostMapper.selectPostById(comment.getEntityId());
        discussionPostMapper.updatePostCommentCount(comment.getEntityId(),discussPost.getCommentCount()+1);
    }
}
