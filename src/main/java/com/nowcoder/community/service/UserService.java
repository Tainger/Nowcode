package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService extends CommunityUtil {

    @Autowired
    private  UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return  userMapper.selectById(id);
    }
    public User findUserByName(String name){
        return  userMapper.selectByName(name);
    }
    public User selectByEmail(String email){
        return  userMapper.selectByEmail(email);
    }

    /**
     *
     * @param user
     * @return
     */
    public Map<String,Object> register(User user){

        Map<String,Object> map = new HashMap<>();
        //空值处理
        //邮箱被注册：
        if(user == null)
            throw new IllegalArgumentException("参数不能为空！");
        if(StringUtils.isEmpty(user.getUsername())) {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isEmpty(user.getPassword())) {
            map.put("passwordMsg","账号密码不能为空");
            return map;
        }
        if(StringUtils.isEmpty(user.getEmail())) {
            map.put("usernameMsg","账号邮箱不能为空");
            return map;
        }

        //观察注册信息是否重复
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("registerMsg","账号已经注册");
            return map;
        }
        user.setType(0);
        user.setSalt(CommunityUtil.generateUUID().substring(0,3));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        //设置用户头象
        //http://images.nowcoder.com/head/%dt.png
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());

        //注册用户：
        userMapper.insertUser(user);
        //更新插入user
        user = userMapper.selectByEmail(user.getEmail());
        Context context = new Context();
        System.err.println(domain);
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+CommunityUtil.generateUUID();
        context.setVariable("name",user.getEmail());
        context.setVariable("url",url);
        String html = templateEngine.process("mail/activation",context);
        mailClient.sendEmail(user.getEmail(),"激活账户",html);
        return map;
    }

    /**
     * 判断激活是否成功
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1)
            return CommunityConstant.ACTIVATION_REPEAT;
        else if(user.getActivationCode()==code){
            //1代表有效
            userMapper.updateUserStatus(1,userId);
            return CommunityConstant.ACTIVATION_SUCCESS;
        }else{
            return  CommunityConstant.ACTIVATION_FAILURE;
        }
    }


    /**
     * 登陆功能只需要账号和密码和过期时间
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */

    //service 主要生成每次业务结果，controller每次根据service生成页面
    public Map<String,Object> login(String username,String password,int expiredSeconds){

        Map<String,Object> map = new HashMap<>();
        //判断username是否是空值
        if(StringUtils.isEmpty(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        //判断password是否是空值。
        if(StringUtils.isEmpty(password)){
            map.put("passwordMsg","账号密码不能为空");
            return  map;
        }
        //验证账号：
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg","账号不存在");
            return map;
        }
        //验证状态
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("usernameMsg","该账号密码错误");
            return map;
        }

        //登录，生称登陆的ticket，返回给浏览器，
        // 下面浏览器每次访问服务器都会携带包含ticket cookie
        //服务器会从服务器里去查，你的时间没有过期，我就默认你是登陆的
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0); //0为有效
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /**
     * 登出
     * @param ticket
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    /**
     * 获取t票
     * @param ticket
     * @return
     */
    public LoginTicket getLoginTicketByTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    /**
     * 修改头像路径
     * @param userId
     * @param headerUrl
     */
    public void updateHeaderUrl(int userId,String headerUrl){
        userMapper.updateUserHead(headerUrl,userId);
    }

    public void updatePassword(int userId,String password){
        userMapper.updateUserPassword(password,userId);
    }

}
