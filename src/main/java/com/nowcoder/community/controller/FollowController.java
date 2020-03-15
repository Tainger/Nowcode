package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){
        int userId = hostHolder.getUser().getId();
        followService.follow(userId,entityType,entityId);
        return CommunityUtil.getJsonString(0,"点赞成功",null);
    }


    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId){
       int userId = hostHolder.getUser().getId();
       followService.unfollow(userId,entityType,entityId);
       return CommunityUtil.getJsonString(0,"取赞成功",null);
    }


    @RequestMapping(path = "/follower/{userId}",method = RequestMethod.GET)
    public String findUserFollowers(@PathVariable int userId, Model model,Page page){
        User user = userService.findUserById(userId);
        if(user==null)
            throw  new  IllegalArgumentException();
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setRows((int)followService.findFollowerRows(userId));
        List<Map<String, Object>> maps= followService.findFollowers(userId,page.getOffset(),page.getLimit());
        User hostUser = hostHolder.getUser();
        //判断每个user和自己的关系是否为关注
        if(maps!=null){
            for (Map<String,Object> map:maps){
                 User u = (User) map.get("user");
                 boolean hasfollow = followService.hasFollower(u.getId(),hostUser.getId());
                 map.put("hasfollow",hasfollow);

            }
        }
        model.addAttribute("maps",maps);
        return "site/follower";
    }

    @RequestMapping(path = "/followee/{userId}",method = RequestMethod.GET)
    public String findUserFollowees(@PathVariable int userId,Model model,Page page){
        User user = userService.findUserById(userId);
        if(user==null)
            throw  new  IllegalArgumentException();
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setRows((int)followService.findFolloweeRows(userId));
        List<Map<String, Object>> maps= followService.findFollowees(userId,page.getOffset(),page.getLimit());
        User hostUser = hostHolder.getUser();
        //判断每个user和自己的关系是否为关注
        if(maps!=null){
            for (Map<String,Object> map:maps){
                User u = (User) map.get("user");
                boolean hasfollow = followService.hasFollower(u.getId(),hostUser.getId());
                    map.put("hasfollow",hasfollow);
            }
        }
        model.addAttribute("maps",maps);
        return "site/followee";
    }
}
