package com.nowcoder.community.event;


import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ElasticService elasticService;
    @Autowired
    private DiscussPostService discussPostService;


    @KafkaListener(topics={TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void comsume(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            logger.error("消息格式错误！");
            return;
        }

        Message message = new Message();
        message.setCreateTime(new Date());
        message.setFromId(SystemFromId);
        message.setToId(event.getEntityUserId());
        Map<String,Object> content = new HashMap();
        content.put("topic",event.getTopic());
        content.put("fromUser",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        for (Map.Entry<String,Object> entry:  event.getData().entrySet()) {
            content.put(entry.getKey(),entry.getValue());
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.insertMessage(message);
    }


    @KafkaListener(topics={TOPIC_PUBLISH})
    public void publishComsume(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            logger.error("消息格式错误！");
            return;
        }
        System.out.println(666);
        DiscussPost discussPost = discussPostService.selectPost(event.getEntityId());
        elasticService.saveDiscussPost(discussPost);
    }
}
