package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussionPostMapper discussionPostMapper;

    public List<Comment> getCommentByEntityTypeAndEntityId(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectComments(entityType,entityId,offset,limit);
    }

    public int getCommentCountByEntityTypeAndEntityId(int entityType,int entityId){
        return commentMapper.getTotalOfComment(entityType,entityId);
    }


    @Transactional()
    public void addComment(Comment comment){

    }

}
