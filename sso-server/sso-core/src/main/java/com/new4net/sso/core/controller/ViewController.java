package com.new4net.sso.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
