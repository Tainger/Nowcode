package com.nowcoder.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/test")
public class TestController {

    @RequestMapping(path = "/school")
    @ResponseBody
    public String getSchool(){
        return "123";
    }


    //GET /student？current=1&limit=20；
    @RequestMapping(path = "/student")
    @ResponseBody
    public String getStudent(@RequestParam(name = "current",defaultValue = "1",required = false) int cu,
                             @RequestParam(name = "limit",defaultValue = "100",required = false) int limit){


        return cu+"..."+limit+"..";
    }
    // /student/123，参数编排到路径里面
    @RequestMapping(path = "/student/{id}")
    @ResponseBody
    public String getPathVariable(@PathVariable("id") int id){

        return id+"..";
    }
    //post  current=1&limit=20;

    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String PostRequest(String name,int age){


        return "success"+name+"...."+age;
    }


    //响应html的两种数据第一种方式
    @RequestMapping(path = "/showstudent",method = RequestMethod.GET)
    public ModelAndView requestShowStudent(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","阿贾");
        modelAndView.addObject("age",13);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    //响应数据第二种方式
    @RequestMapping(path = "/showstudent2",method = RequestMethod.GET)
    public String requestShowStudent2(Model model){
        model.addAttribute("name","阿园");
        model.addAttribute("age","16");
        return "/demo/view";
    }

    //响应json数据的形式
    @RequestMapping(path = "/getjson",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getjsonshow(){
        List<Map<String,Object>> list =new ArrayList<>();

        Map<String,Object> map = new HashMap<>();
        map.put("你好","漂亮");
        map.put("你别","别这样");
        Map<String,Object> map1 = new HashMap<>();
        map1.put("你肥嘟嘟","zhende1");
        map1.put("你搜索","好了");
        list.add(map1);
        list.add(map);
        return list;
    }




}
