package com.wa.cluemrg.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Secured("ROLE_LEVEL1")
@RequestMapping("/security")
public class SecurityController {

    @GetMapping("/getCurrentUsername")
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 获取当前认证的用户名
        String username = authentication.getName();
        return username;
    }
}

