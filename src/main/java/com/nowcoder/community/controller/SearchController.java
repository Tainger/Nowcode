package com.nowcoder.community.controller;


import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.ElasticService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ElasticService elasticService;


    @RequestMapping(path = "/search")
    public String searchPost(Page page, String keyword, Model model){
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticService.searchService(keyword,page.getCurrent()-1,page.getLimit());
        if(searchResult!=null) {
            List list = new ArrayList<HashMap<String, Object>>();
            for (DiscussPost post : searchResult) {
                Map map = new HashMap<String, Object>();
                User user = userService.findUserById(post.getUserId());
                long count = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("user", user);
                map.put("count", count);
                map.put("post", post);
                list.add(map);
            }
            model.addAttribute("posts", list);
        }
        page.setRows(searchResult==null?0: (int) searchResult.getTotalElements());
        page.setPath("/search?keyword="+keyword);
        return "site/search";
    }


}


