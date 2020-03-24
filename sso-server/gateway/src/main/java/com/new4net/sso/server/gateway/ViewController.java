package com.new4net.sso.server.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ViewController {
    @GetMapping(value = "/login")
    public String login(HttpServletRequest req) {
        return "login";
    }
    @GetMapping(value = "/")
    public String login1(HttpServletRequest req) {
        return "login";
    }
    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping("/checkVCode")
    @ResponseBody
    public boolean checkVCode(HttpServletRequest req, @RequestParam("vCode") String vCode) {
        String vCodeId = "";
        if(((HttpServletRequest)req).getCookies()!=null){
            for(Cookie cookie:((HttpServletRequest)req).getCookies()){
                if("vCodeId".equals(cookie.getName())){
                    vCodeId = cookie.getValue();
                    break;
                }
            }
        }

        String kaptchaExpected = (String) redisTemplate.opsForValue().get(vCodeId);
        if (kaptchaExpected == null || vCode == null || !kaptchaExpected.toUpperCase().equals(vCode.toUpperCase())) {
            return false;
        }
        return true;
    }
}
