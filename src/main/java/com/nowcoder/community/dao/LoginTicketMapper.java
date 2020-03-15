package com.nowcoder.community.dao;


import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LoginTicketMapper {

    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);
//    @Update({
//            "<script>",
//            "update login_ticket set status = #{arg1} where ticket = #{arg0} ",
//            "<if test = \"ticket!=null\">",
//            "and 1 = 1",
//            "</if>",
//            "</script>"
//    })
    int updateStatus(@Param("ticket") String ticket,
                     @Param("status") int status);
}
