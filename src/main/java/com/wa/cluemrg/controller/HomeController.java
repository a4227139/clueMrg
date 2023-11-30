package com.wa.cluemrg.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Secured("ROLE_LEVEL1")
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "caseGraph.html"; // 这里返回的是主页的视图名
    }

    @GetMapping("/getCurrentUsername")
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 获取当前认证的用户名
        String username = authentication.getName();
        return username;
    }
}

