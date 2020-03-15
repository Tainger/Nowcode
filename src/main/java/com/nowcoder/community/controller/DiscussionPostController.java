package com.nowcoder.community.controller;


import com.nowcoder.community.entity.*;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussionPostController implements CommunityConstant{

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addPost(String title,String content){
        User user = hostHolder.getUser();
        if(user ==null){
            return CommunityUtil.getJsonString(403,"你还没有这个权限");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(0);
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setScore(0.0);
        discussPost.setType(0);
        discussPostService.insertDiscussPost(discussPost);

        Event event = new Event();
        event.setUserId(hostHolder.getUser().getId())
                .setEntityId(discussPost.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setTopic(TOPIC_PUBLISH);
                //没必要设置post可以直接从数据库中查找。
//                .setData("post",discussPost);
        eventProducer.produce(event);


        return  CommunityUtil.getJsonString(0,"发部成功");
    }


    @RequestMapping("/detail/{postId}")
    public String postDetail(@PathVariable("postId") int postId, Model model, Page page){
        //dispost帖子
        DiscussPost discussPost = discussPostService.selectPost(postId);
        //user使用
        User user =userService.findUserById(discussPost.getUserId());

        model.addAttribute("post",discussPost);
        model.addAttribute("user",user);

        page.setLimit(5);
        int commentCount=commentService.getCommentCountByEntityTypeAndEntityId(ENTITY_TYPE_POST, postId);
        page.setRows(commentCount);
        page.setPath("/discuss/detail/"+postId);

        List<Comment> comments =commentService.getCommentByEntityTypeAndEntityId(ENTITY_TYPE_POST,postId,page.getOffset(),page.getLimit());
        //每条评论还要显示一个人的头像
        List<Map<String,Object>> commentVos = new ArrayList<>();
        if(comments!=null){
            for(Comment comment:comments){
                Map<String,Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                List<Map<String,Object>> replyVos= new ArrayList<>();
                List<Comment> replyList= commentService.getCommentByEntityTypeAndEntityId(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                int replyCount = commentService.getCommentCountByEntityTypeAndEntityId(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);
                if(replyList!=null){
                    for(Comment replyComment:replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        replyVo.put("replyComment",replyComment);
                        User Commentuser =userService.findUserById(replyComment.getUserId());
                        replyVo.put("user",Commentuser);
                        User targetUser=userService.findUserById(replyComment.getTargetId());
                        replyVo.put("target",targetUser);
                        replyVos.add(replyVo);
                    }
                    commentVo.put("replyVos",replyVos);
                }
                commentVos.add(commentVo);
            }
        }
        model.addAttribute("commentVos",commentVos);
        model.addAttribute("commentCount",commentCount);
        return  "site/discuss-detail";
    }


}
