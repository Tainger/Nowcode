package com.nowcoder.community.service;


import com.nowcoder.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import static com.nowcoder.community.util.RedisUtil.getEntityLikeKey;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 给某个帖子点赞的同时，同时帖子的人也点个赞。
     * @param entityType
     * @param entityId
     * @param userId
     */
    public void like(int entityType,int entityId,int userId,int entityUserId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey =RedisUtil.getUserLikeKey(entityUserId);
                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey,userId);
                redisOperations.multi();
                if(isMember){
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else {
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });

    }

    /**
     * 判断一个人是否点赞
     * @param entityType
     * @param entityId
     * @param userId
     * @return
     */
    public int findEntityLikeOrNot(int entityType,int entityId,int userId){
        String key = getEntityLikeKey(entityType,entityId);
        if(redisTemplate.opsForSet().isMember(key,userId)) {
            return 0;
        }
        else {
            return 1;
        }
    }
    /**
     * 查询一个帖子点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public Long findEntityLikeCount(int entityType, int entityId){
        String key = getEntityLikeKey(entityType,entityId);
        return  redisTemplate.opsForSet().size(key);
    }

    /**
     * 查询一个帖子点赞的数量
     * @return
     */
    public int userLikeCount(int userId){
        String key = RedisUtil.getUserLikeKey(userId);
        Integer count= (Integer) redisTemplate.opsForValue().get(key);
        return count==null?0:count.intValue();
    }

}
