package com.new4net.sso.server.gateway;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
public class VCodeFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        try {
            RequestContext context = RequestContext.getCurrentContext();
            HttpServletRequest request = context.getRequest();
            if("/api/login".equals(request.getRequestURI())){
                String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
                String username = null, password = null,vCode=null;
                if(StringUtils.hasText(body)) {
                    JSONObject jsonObj = JSON.parseObject(body);
                    username = jsonObj.getString("username");
                    password = jsonObj.getString("password");
                    vCode = jsonObj.getString("vCode");
                }
                // 1. 进行验证码的校验
                validate(request,vCode);
            }

        }catch (Exception e){
            throw  new ZuulException(e,500,e.getMessage());
        }

        return null;
    }

    private void validate(HttpServletRequest request,String vCode) throws ValidateCodeException {
        String kaptchaExpected = (String) request.getSession()
                .getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
        if (kaptchaExpected == null || vCode == null || !kaptchaExpected.toUpperCase().equals(vCode.toUpperCase())) {
            throw new ValidateCodeException("验证码错误");
        }

    }
}
