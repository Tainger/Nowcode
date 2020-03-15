package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant{

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserService userService;
    //关注了多少人，被关注了多少人
    public void follow(int userId,int entityType,int entityId){
        //关注别人的只能是user
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followerKey = RedisUtil.getFollowerKey(entityType,entityId);
                String followeeKey = RedisUtil.getFolloweeKey(ENTITY_TYPE_USER,userId);
                redisOperations.multi();
                redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                return redisOperations.exec();
            }
        });
    }//关注了多少人，被关注了多少人
    public void unfollow(int userId,int entityType,int entityId){
        //A关注userid
        //求关注userd的数量
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followerKey = RedisUtil.getFollowerKey(entityType,entityId);
                String followeeKey = RedisUtil.getFolloweeKey(ENTITY_TYPE_USER,userId);
                redisOperations.multi();
                redisOperations.opsForZSet().remove(followerKey,userId);
                redisOperations.opsForZSet().remove(followeeKey,entityId);
                return redisOperations.exec();
            }
        });

    }

    /**
     * 多少人关注了user
     * @param followeeId
     * @return
     */
    public long followerCount(int followeeId){
        String followerKey = RedisUtil.getFollowerKey(ENTITY_TYPE_USER,followeeId);
        return redisTemplate.opsForZSet().size(followerKey);
    }
    /**
     * followedUserId关注多少人
     */
    public long followeeCount(int  userId){
        String followeeKey = RedisUtil.getFolloweeKey(ENTITY_TYPE_USER,userId);
        return redisTemplate.opsForZSet().size(followeeKey);
    }

    /**
     * userId被哪些人关注
     * @param
     * @return
     */
    public boolean hasFollower(int followeeId,int followerId){
        String followerKey = RedisUtil.getFollowerKey(ENTITY_TYPE_USER,followeeId);
        return redisTemplate.opsForZSet().score(followerKey,followerId)==null?false:true;
    }

    /**
     * followeeId
     * @param
     * @param
     * @return
     */
    public boolean hasFollowee(int followeeId,int followerId){
        String followeeKey = RedisUtil.getFolloweeKey(ENTITY_TYPE_USER,followerId);
        return redisTemplate.opsForZSet().score(followeeKey,followeeId)==null?false:true;
    }

    /**
     * 获取关注自己的人
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String,Object>> findFollowers(int userId, int offset, int limit){
        String followersKey = RedisUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followersKey,offset,offset+limit-1);
        List<Map<String,Object>> maps = new ArrayList<>();
        if(ids!=null){
            for(Integer id:ids){
                User user = userService.findUserById(id);
                double time=redisTemplate.opsForZSet().score(followersKey,id);
                Map map = new HashMap();
                map.put("user",user);
                map.put("date",new Date((long)time));
                maps.add(map);
            }
        }
        return maps;
    }

    /**
     * 获取关注的人
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String,Object>> findFollowees(int userId, int offset, int limit){
        String followeesKey = RedisUtil.getFolloweeKey(ENTITY_TYPE_USER,userId);
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followeesKey,offset,offset+limit-1);
        List<Map<String,Object>> maps = new ArrayList<>();
        if(ids!=null){
            for(Integer id:ids){
                User user = userService.findUserById(id);
                double time=redisTemplate.opsForZSet().score(followeesKey,id);
                Map map = new HashMap();
                map.put("user",user);
                map.put("date",new Date((long)time));
                maps.add(map);
            }
        }
        return maps;
    }

    /**
     * 获取粉丝的人数
     * @param userId
     * @return
     */
    public long findFollowerRows(int userId) {
        String key = RedisUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取关注的人数
     * @param userId
     * @return
     */
    public long findFolloweeRows(int userId) {
        String key = RedisUtil.getFolloweeKey(ENTITY_TYPE_USER,userId);
        return redisTemplate.opsForZSet().size(key);
    }
}
