package com.nowcoder.community.dao;


import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface   DiscussionPostMapper {

    List<DiscussPost> selectUserPosts(@Param("userId")int userId,@Param("offset") int offset,@Param("limit") int limit);

    //如果只有一个参数，并且在<if>里面使用，则必须加别名，为什么？
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectPostById(int postId);

    int updatePostCommentCount(@Param("postId") int postId,
                               @Param("commentCount")int commentCount);
}
