package com.new4net.sso.server.gateway;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.google.code.kaptcha.util.Config;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MyKaptchaServlet extends KaptchaServlet {
    private Properties props = new Properties();
    private Producer kaptchaProducer = null;
    private String sessionKeyValue = null;
    private String sessionKeyDateValue = null;
    private RedisTemplate redisTemplate;

    public MyKaptchaServlet(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void init(ServletConfig conf) throws ServletException {
        super.init(conf);
        ImageIO.setUseCache(false);
        Enumeration initParams = conf.getInitParameterNames();

        while(initParams.hasMoreElements()) {
            String key = (String)initParams.nextElement();
            String value = conf.getInitParameter(key);
            this.props.put(key, value);
        }

        Config config = new Config(this.props);
        this.kaptchaProducer = config.getProducerImpl();
        this.sessionKeyValue = config.getSessionKey();
        this.sessionKeyDateValue = config.getSessionDate();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setDateHeader("Expires", 0L);
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
        resp.setHeader("Pragma", "no-cache");
        resp.setContentType("image/jpeg");
        String capText = this.kaptchaProducer.createText();
        String vCodeId = "";
        if(((HttpServletRequest)req).getCookies()!=null){
            for(Cookie cookie:((HttpServletRequest)req).getCookies()){
                if("vCodeId".equals(cookie.getName())){
                    vCodeId = cookie.getValue();
                    break;
                }
            }
        }

        if(StringUtils.isEmpty(vCodeId)){

            Cookie cookie = new Cookie("vCodeId", UUID.randomUUID().toString());
            vCodeId=cookie.getValue();
            resp.addCookie(cookie);
        }
        System.out.println(vCodeId);
        System.out.println(capText);

        redisTemplate.opsForValue().set(vCodeId,capText,60, TimeUnit.SECONDS);

        BufferedImage bi = this.kaptchaProducer.createImage(capText);
        ServletOutputStream out = resp.getOutputStream();

        ImageIO.write(bi, "jpg", out);
    }

}
