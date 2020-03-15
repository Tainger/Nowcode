package com.nowcoder.community;


import com.nowcoder.community.dao.LoginTicketMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestRedis {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testString(){
        String redisKey="test:count";
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testupdate(){
        loginTicketMapper.updateStatus("0a045655ee744a2a86c2ff3c9894618c",1);
    }
    @Test
    public void testHash(){
        String redisKey="test:Hash";
        redisTemplate.opsForHash().put(redisKey,"username","贾志远");
        redisTemplate.opsForHash().put(redisKey,"id",1);
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
    }

    @Test
    public void testList(){
        String redisKey = "test:list";
        redisTemplate.opsForList().leftPush(redisKey,1);
        redisTemplate.opsForList().leftPush(redisKey,"2");
        redisTemplate.opsForList().leftPush(redisKey,3);
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,1));
    }

    @Test
    public void testSet(){
        String redisKey = "test:set";
        redisTemplate.opsForSet().add(redisKey,"你好");
        redisTemplate.opsForSet().add(redisKey,"真相");
        }
    @Test
    public void testTransactional(){
        Object ob =redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) {
                String redisKey="test:tx";
                redisOperations.multi();
                redisOperations.opsForSet().add(redisKey,"123");
                redisOperations.opsForSet().add(redisKey,"456");
                redisOperations.opsForSet().add(redisKey,"789");
                System.out.println(redisOperations.opsForSet().members(redisKey));
                return redisOperations.exec();
            }
        });
        System.out.println(ob);
    }
}
