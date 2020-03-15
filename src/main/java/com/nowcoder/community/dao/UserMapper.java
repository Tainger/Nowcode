package com.nowcoder.community.dao;


import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    // 根据序号选择user
    User selectById(int id);
    //根据名字选择user
    User selectByName(String name);
    //根据email选择user
    User selectByEmail(String email);
    //插入user
    int insertUser(User user);
    //修改用户状态
    int updateUserStatus(int status,int id);
    //修改用户头像
    int updateUserHead(String headerUrl,int id);
    //修改用户密码
    int updateUserPassword(String password,int id);

}
