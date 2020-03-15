package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {


    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/message/list",method = RequestMethod.GET)
    public String getMessagePage(Model model, Page page){
        int id = hostHolder.getUser().getId();
        page.setRows(messageService.getSessionCount(id));
        page.setLimit(10);
        List<Message> messageList = messageService.getSessionNewestMessageList(id,page.getOffset(),page.getLimit());
        List<Map<String,Object>> list = new ArrayList<>();
        for(Message message:messageList){
            Map<String,Object> map = new HashMap<>();
            int targetId = id==message.getFromId()?message.getToId():message.getFromId();
            map.put("conversation",message);
            map.put("targetUser",userService.findUserById(targetId));
            map.put("messageUnread",messageService.getMessageUnReadCount(id,message.getConversationId()));
            map.put("totalMessageOfConversation",messageService.getMessageCount(message.getConversationId()));
            list.add(map);
        }
        model.addAttribute("totalMessage",messageService.getMessageUnReadCount(id,null));
        model.addAttribute("vos",list);
        return "site/letter";
    }

    @RequestMapping(path = "/message/detail/{id}",method = RequestMethod.GET)
    public String getMessageDetail(Model model,Page page,@PathVariable("id")String conversationId){
        page.setRows(messageService.getMessageCount(conversationId));
        page.setLimit(10);
        page.setPath("/message/detail/"+messageService);
        List<Message> list = messageService.getMessageList(conversationId,page.getOffset(),page.getLimit());
        model.addAttribute("messages",list);
        return "site/letter-detail";
    }
}
