package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    //如果注入的属性与Page的属性相匹配就自动注入。提交的参数current是page中的属性。
    public String getIndexPage(Model model, Page page) {
        // 方法调用钱,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setRows(discussPostService.findDiscussionPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.findPostByUserId(0,page.getOffset(),page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                long count = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                map.put("likeCount",count);
                discussPosts.add(map);
            }
        }
        int messageCount = messageService.getMessageUnReadCount(hostHolder.getUser().getId(),null);
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("messageCount", messageCount);
        return "index";
    }

}