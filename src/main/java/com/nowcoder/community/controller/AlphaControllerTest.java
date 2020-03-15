package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class AlphaControllerTest {


    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/demo/ajax")
    public String getdemoHtml(){
        return "site/demo-ajax";
    }

    @RequestMapping("/ajax")
    @ResponseBody
    public String ajaxtest(HttpSession session){
        User user = new User();
        user.setUsername("老妇人");
        Map<String, Object> map = new HashMap<>();
        map.put("第一夫人",user);
        map.put("测试","曹氏");
        return CommunityUtil.getJsonString(1,"信号不好",map);
    }
    @RequestMapping("/map")
    @ResponseBody
    public String Test(){
        return "你好！";
    }




    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){

    }


    // Get 请求
    // /student？current=1&limit=20；
    @RequestMapping(path = "/students")
    @ResponseBody
    public String getStudent(
            @RequestParam(name = "current",required = false,defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false,defaultValue = "20") int limit){
        return  "Student"+current+"..."+limit;
    }

    // /student/123，参数编排到路径里面
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return id+"...";
    }


    //Post
    @RequestMapping(value = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "成功";
    }

    //响应html数据
    @RequestMapping(value = "/teacher")
    public ModelAndView getTeacher(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("name","老哥");
        mv.addObject("age","16");
        //因为他已经知道这里的文件view.html,所以不用变成view.html
        mv.setViewName("/demo/view");
        return  mv;
    }

    //获取html数据
    @RequestMapping(path = "/school1",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age","80");
        return "demo/view";
    }


    //向浏览器响应json数据(异步请求)
    //java对象  ->JSON字符串->JS对象

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public List<HashMap<String,Object>> getEmp(){
        HashMap<String,Object> map =new HashMap();
        map.put("name","张三");
        map.put("age","23");
        map.put("salary","8000");
        HashMap<String,Object> map1 =new HashMap();
        map1.put("name","张四");
        map1.put("age","21");
        map1.put("salary","7000");
        List<HashMap<String,Object>> list = new ArrayList<>();
        list.add(map);
        list.add(map1);
        return list;
    }



    //cookie
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //  设置cookie的头文字节点。
        cookie.setPath("/community/ajiax");
        cookie.setMaxAge(60*10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(HttpServletRequest request){
        Cookie cookie[]=request.getCookies();
        for (Cookie c:cookie){
            System.out.println(c.getName()+"...."+c.getValue());
        }
        return "get cookie";
    }

    @RequestMapping(path = "/cookie/get1",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie1(@CookieValue String code){
        System.out.println(code);
        return "...";
    }
    //session示例
    @RequestMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id","1");
        session.setAttribute("name","Test");
        return "set session";
    }

    @RequestMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "set session";
    }



}
