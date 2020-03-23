package com.new4net.sso.server.gateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @RequestMapping("/checkVCode")
    @ResponseBody
    public boolean checkVCode(HttpServletRequest req, @RequestParam("vCode") String vCode) {
        String kaptchaExpected = (String) req.getSession()
                .getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
        if (kaptchaExpected == null || vCode == null || !kaptchaExpected.toUpperCase().equals(vCode.toUpperCase())) {
            return false;
        }
        return true;
    }

}
