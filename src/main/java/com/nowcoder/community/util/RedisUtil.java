package com.nowcoder.community.util;

public class RedisUtil {

    private  static final String SPLIT =":";
    private  static  final  String  PREFIX_ENTITY_LIKE= "like:entity";
    private  static  final  String  PREFIX_USER_LIKE= "like:user";
    //A关注user
    private static final String PREFIX_USER_FOLLOWER="follower:user";
    //user关注A
    private static final String PREFIX_USER_FOLLOWEE="followee:entityId";
    //某个实体的赞
    //like:entity:entityType:entityId ->UserId

    public static String  getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }
    //某个人受关注的数量
    public static String  getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }
    /**
     * @param
     * @return
     */
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_USER_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
    /**
     * @param
     * @return
     */
    public static String getFolloweeKey(int entityType,int entityId){
        return PREFIX_USER_FOLLOWEE+SPLIT+entityType+SPLIT+entityId;
    }
}
