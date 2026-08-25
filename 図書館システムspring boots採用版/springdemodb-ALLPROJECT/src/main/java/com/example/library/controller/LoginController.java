package com.example.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * http://localhost:8080/login へのアクセスでログイン画面を表示する
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}