package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    //域名
    @Value("${community.path.domain}")
    private String domainPath;
    //文件上传路径名
    @Value("${community.path.upload}")
    private String uploadPath;
    //文件访问名
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingHtml() {
        return "site/setting";
    }


    @RequestMapping(path = "/upload")
    public String setHeadUrl(MultipartFile headerImage, Model model) throws Exception {
        if (headerImage == null) {
            model.addAttribute("error", "文件为空");
            return "site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        if (suffix == null) {
            model.addAttribute("error", "非法文件");
            return "site/setting";
        }
        //生成随机文件名称
        fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + fileName);

        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.info("上传文件失败" + e.getMessage());
            throw new Exception("上传文件失败，服务器发生异常", e);
        }

        //http://localhost:8080/community/user/header/xxx.png
        String headUrl = domainPath + contextPath + "/user" + "/header/" + fileName;
        User user = hostHolder.getUser();
        userService.updateHeaderUrl(user.getId(), headUrl);
        return "redirect:/index";
    }


    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        String sourceFile = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        //设置图片返回的类型
        response.setContentType("image/" + suffix);
        byte buffer[] = new byte[1024];
        int b;
        OutputStream os = null;
        InputStream is = null;
        try {
            is = new FileInputStream(sourceFile);
            //response 会自动关闭
            os = response.getOutputStream();
            while ((b = is.read(buffer)) != -1) {
                //缓冲去 ，长度， b
                os.write(buffer, 0, b);
            }
        } catch (Exception e) {
            logger.info("头像输出" + e.getMessage());
        } finally {
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                logger.info("输入输出流关闭" + e.getMessage());
            }
        }
    }

    @RequestMapping(path = "/updatepasswrod", method = RequestMethod.POST)
    public String updatepasswrod(String oriPassword, String onePassword,
                                 @CookieValue("ticket") String ticket,String twoPassword, Model model){
        if(oriPassword==null){
            model.addAttribute("oriPasswordError","原密码不能为空");
            return "site/setting";
        }
        if(onePassword.length()<8){
            model.addAttribute("curPasswordError","密码长度太短");
            return "site/setting";
        }
        User user = hostHolder.getUser();
        String salt = user.getSalt();
        String saltpassword= CommunityUtil.md5(oriPassword+salt);
        if(!saltpassword.equals(user.getPassword())){
            model.addAttribute("oriPasswordError","原密码不正确");
            return "site/setting";
        }

        if(!onePassword.equals(twoPassword)){
            model.addAttribute("oneTwoPasswordError","两次密码错误");
            return "site/setting";
        }
        userService.updatePassword(user.getId(),CommunityUtil.md5(onePassword+user.getSalt()));
        //原先的ticket无效
        userService.logout(ticket);
        return "redirect:/login";
    }

    @RequestMapping(path = "/profile/{userid}", method = RequestMethod.GET)
    public String getProfile(@PathVariable("userid") int userId,Model model){
        int userLikeCount = likeService.userLikeCount(userId);
        long followerCount = followService.followerCount(userId);
        long followeeCount = followService.followeeCount(userId);
        boolean hasFollower = followService.hasFollower(userId,hostHolder.getUser().getId());
        model.addAttribute("user",userService.findUserById(userId));
        model.addAttribute("userLikeCount",userLikeCount);
        model.addAttribute("followerCount",followerCount);
        model.addAttribute("followeeCount",followeeCount);
        model.addAttribute("hasFollower",hasFollower);
        return "site/profile";
    }
}
