package com.nowcoder.community.util;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    /**
     * 获取cookie中的值
     * @param request
     * @param name
     * @return
     */
    public static String getCookieValue(HttpServletRequest request,String name){
        if(request==null||name==null)
            throw new IllegalArgumentException();
        Cookie cookies[] = request.getCookies();
        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name))
                    return cookie.getValue();
            }
        }
        return null;
    }


}
