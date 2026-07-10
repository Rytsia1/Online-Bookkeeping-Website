package com.DTMK.Online.Bookkeeping.Website.Project.controller;

import com.DTMK.Online.Bookkeeping.Website.Project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- ENDPOINT REGISTER ---
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        return userService.registerUser(username, password);
    }

    // --- ENDPOINT LOGIN (BARU) ---
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return userService.loginUser(username, password);
    }
}