package com.sso.login.utils;

import org.springframework.stereotype.Component;
import pojo.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginCacheUtil {
    //存登录用户
    public static Map<String, User> loginUser=new HashMap<>();
}
