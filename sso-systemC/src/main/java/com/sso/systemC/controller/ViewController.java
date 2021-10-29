package com.sso.systemC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/view")
public class ViewController {

    @Autowired
    private RestTemplate restTemplate;
    private final String USER_INFO_ADDRESS="http://login.codeshop.com:9000/login/info?token=";

    @GetMapping("/index")
    public String toIndex(@CookieValue(required = false,value = "TOKEN") Cookie cookie, HttpSession session){
        String target = "";
        if(cookie!=null){
            //若已登录，则允许访问
            String token=cookie.getValue();
            if(!StringUtils.isEmpty(token)){
                Map result = restTemplate.getForObject(USER_INFO_ADDRESS + token, Map.class);
                //保存已登录用户
                session.setAttribute("loginUser",result);
                return "index";
            }
        }else {
            //未登录，跳转到登录界面
            target="http://login.codeshop.com:9000/view/login?target=http://systemC.codeshop.com:9011/view/index";
        }
        return "redirect:"+target;
    }
}
