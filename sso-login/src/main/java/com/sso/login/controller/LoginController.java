package com.sso.login.controller;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.sso.login.utils.LoginCacheUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;
import pojo.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/login")
public class LoginController {

    //连接mongodb数据库
    MongoClient mongoClient=new MongoClient("localhost",27017);
    DB db= (DB) mongoClient.getDatabase("my_user");
    //创建新用户
    //MongoCredential credential = new MongoCredential("zhangsan","my_user","12345".toCharArray());

    //模拟账号登录
    private static Set<User> dbUsers;
    static {
        dbUsers = new HashSet<>();
        dbUsers.add(new User(0,"zhangsan","12345"));
        dbUsers.add(new User(1,"lisi","123456"));
        dbUsers.add(new User(2,"wangwu","1234567"));
    }

    @PostMapping
    public String doLogin(User user, HttpSession session, HttpServletResponse response){

        String target=(String) session.getAttribute("target");

        //模拟数据库通过登录用户名和密码去查找数据库的用户，判断用户是否登录，保存用户登录信息
        Optional<User> first=dbUsers.stream().filter(dbUsers -> dbUsers.getUsername().equals(user.getUsername()) &&
                dbUsers.getPassword().equals(user.getPassword()))
                .findFirst();
        //判断用户是否登录
        if(first.isPresent()){
            //保存用户登录信息
            String token = UUID.randomUUID().toString();
            Cookie cookie=new Cookie("TOKEN",token);
            //存入子系统
            cookie.setDomain("codeshop.com");
            response.addCookie(cookie);
            LoginCacheUtil.loginUser.put(token,first.get());
        }else{
            //登录失败
            session.setAttribute("msg","用户名或密码错误");
            return "login";
        }
        //重定向到 target地址
        return "redirect:"+target;
    }

    //服务器之间交互
    @ResponseBody
    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(String token){
        //判断是否登录
        if (!StringUtils.isEmpty(token)){
            User user=LoginCacheUtil.loginUser.get(token);
            return ResponseEntity.ok(user);
        }else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    //退出登录
    @GetMapping("/logout")
    public String logout(@CookieValue(value = "TOKEN")Cookie cookie,HttpServletResponse response,String target){
        response.reset();
        //cookie过期
        cookie.setMaxAge(0);
        //清除缓存
        LoginCacheUtil.loginUser.remove(cookie.getValue());
        return "redirect:"+target;
    }
}
