package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private  String contextPath;

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterHtml(){
        return "site/register";
    }

    @RequestMapping(path = "/login")
    public String toLoginHtml(){
        return "site/login";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        //账号，密码，邮箱
        Map map =userService.register(user);
        //
        if(map==null||map.isEmpty()){
           model.addAttribute("msg","注册成功，我们已经向您的邮箱发送那个一个邮件，请你及时激活");
           model.addAttribute("target","/index");
           return  "site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("registerMsg",map.get("registerMsg"));
            return "site/register";
        }

    }


    //http://localhost:8080/community/activation/id/code
    @RequestMapping(path = "/activation/{id}/{code}")
    public String activationMap(Model model, @PathVariable("id") int id,
                                @PathVariable("code") String code){
        int res =userService.activation(id,code);
        if(res==CommunityConstant.ACTIVATION_SUCCESS){
            model.addAttribute("msg","你的账号激活成功");
            model.addAttribute("target","/login");
        }else if(res==CommunityConstant.ACTIVATION_REPEAT){
            model.addAttribute("msg","你的账号已经激活了");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","你的账号激活失败");
            model.addAttribute("target","/index");
        }
        return "site/operate-result";
    }

    @RequestMapping(path = "/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){

        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //放入session，生成session id存在session的id中。
        session.setAttribute("kaptcha",text);

        //将图片输出的浏览器
        response.setContentType("image/png");

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        }catch (Exception e){
            logger.error("响应验证码"+e.getMessage());
        }
    }

    //当请求验证码的时候，验证码已经写入session里了，当登陆的时候重新校验

    /**
     *
     * @param model
     * @param session
     * @param response
     * @param code
     * @param username
     * @param password
     * @param rememberme
     * @return
     */
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    /*
            //String code,String username,String password
            //如果不是单个的参数而是user,会自动放在model
            //request请求中也会存放该对象
     */
    public String login(String code,String username,String password,boolean rememberme,
                        Model model, HttpSession session, HttpServletResponse response
                        ){

        Map<String,Object> map ;
        //检查验证码
        String text = (String) session.getAttribute("kaptcha");
        if(StringUtils.isEmpty(text)||StringUtils.isEmpty(code)||!text.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "site/login";
        }
        int expiredSeconds = rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SESSION;
        map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout("ticket");
        //默认get
        return "redirect:/login";
    }
}
