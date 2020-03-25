package com.new4net.sso.server.gateway;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.new4net.util.AjaxMsg;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

public class VCodeFilter extends OncePerRequestFilter {

    private final RedisTemplate redisTemplate;

    public VCodeFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(HttpMethod.POST.equals(request.getMethod())){
            String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
            String username = null, password = null,vCode=null;
            if(StringUtils.hasText(body)) {
                JSONObject jsonObj = JSON.parseObject(body);
                vCode = jsonObj.getString("vCode");
            }

            String vCodeId = "";
            for(Cookie cookie:((HttpServletRequest)request).getCookies()){
                if("vCodeId".equals(cookie.getName())){
                    vCodeId = cookie.getValue();
                    break;
                }
            }

            String kaptchaExpected = (String) redisTemplate.opsForValue().get(vCodeId);

            System.out.println(kaptchaExpected);

            if (kaptchaExpected == null || vCode == null || !kaptchaExpected.toUpperCase().equals(vCode.toUpperCase())) {
                response.getWriter().write(JSONObject.toJSONString(new AjaxMsg("-10","验证码错误")));
            }
        }
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
