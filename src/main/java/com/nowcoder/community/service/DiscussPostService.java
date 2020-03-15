package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    DiscussionPostMapper discussionPostMapper;
    @Autowired
    SensitiveFilter sensitiveFilter;

    /**
     * 分页查询一个人发的帖子
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<DiscussPost> findPostByUserId(int userId,int offset,int limit){
        return  discussionPostMapper.selectUserPosts(userId,offset,limit);
    }

    /**
     * 一个人发了多少帖子
     * @param userId
     * @return
     */
    public int findDiscussionPostRows(int userId){
        return discussionPostMapper.selectDiscussPostRows(userId);
    }


    public int insertDiscussPost(DiscussPost discussPost){

        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));

        return discussionPostMapper.insertDiscussPost(discussPost);
    }

    /**
     * 查询post
     * @param postId
     * @return
     */
    public DiscussPost selectPost(int postId){
         return  discussionPostMapper.selectPostById(postId);
    }
}
