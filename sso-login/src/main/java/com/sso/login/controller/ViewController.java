package com.sso.login.controller;

import com.sso.login.utils.LoginCacheUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;
import pojo.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/view")
public class ViewController {
    /**
     * 跳转到登录界面
     * @return
     */
    @GetMapping("/login")
    public String toIndex(@RequestParam(required = false,defaultValue = "")String target,
                          HttpSession session,@CookieValue(required = false,value = "TOKEN") Cookie cookie){
        if(StringUtils.isEmpty(target)){
            //默认跳转到系统C
            target="http://systemC.codeshop.com:9011/view/index";
        }
        //如果已经登录的用户再次访问登录系统时，就要重定向
        if(cookie != null){
            String value=cookie.getValue();
            User user= LoginCacheUtil.loginUser.get(value);
            if(user!=null){
                return "redirect:"+target;
            }
        }

        //退出登录，清空，跳转登录界面
        session.removeAttribute("msg");
        if(StringUtils.isEmpty(target)){
            target="http://login.codeshop.com:9000/view/login";
        }

        //TODO:要做target地址是否合法的校验
        //重定向地址
        session.setAttribute("target",target);
        return "login";
    }
}
