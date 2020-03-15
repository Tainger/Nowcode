package com.nowcoder.community.controller;


import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller()
public class likeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId){
        int userId =hostHolder.getUser().getId();
        likeService.like(entityType,entityId,userId,entityUserId);
        //0表示已赞  1 表示未赞
        int likeStatus = likeService.findEntityLikeOrNot(entityType,entityId,userId);
        long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("likeStatus",likeStatus);
        map.put("likeCount",likeCount);
        return CommunityUtil.getJsonString(0,null,map);
    }


}
