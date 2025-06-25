package com.onlinecourses.controller;


import com.onlinecourses.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/me")
    public String currentUser(@AuthenticationPrincipal User user) {
        return "Hello, " + user.getName() + " (" + user.getRole() + ")";
    }
}
