package com.example.library.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.library.entity.Account;
import com.example.library.repository.AccountRepository;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    public String handleAuth(@RequestParam String action,
                             @RequestParam(value = "userId", required = false) String userIdStr,
                             @RequestParam(required = false) String password,
                             HttpSession session,
                             Model model) {

        if ("login".equals(action)) {
            int userId = -1;
            try {
                if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                    userId = Integer.parseInt(userIdStr);
                }
            } catch (NumberFormatException e) {
                model.addAttribute("error", "User IDは半角数字で入力してください。");
                return "login";
            }

            Account account = accountRepository.findByUserIdAndUserPassword(userId, password).orElse(null);
            if (account != null) {
                session.setAttribute("loginUser", account);
                return "redirect:/books";
            } else {
                model.addAttribute("error", "IDまたはパスワードが不正です。");
                return "login";
            }

        } else if ("signin".equals(action)) {
            if (password == null || !password.matches("^[a-zA-Z0-9]{1,10}$")) {
                model.addAttribute("error", "パスワードは半角英数字10文字以内で設定してください。");
                return "login";
            }

            Account newAccount = new Account();
            newAccount.setUserPassword(password);
            newAccount.setRole("User");

            Account savedAccount = accountRepository.save(newAccount);
            model.addAttribute("message", "登録成功！あなたのUser_IDは: " + savedAccount.getUserId() + " です。");
            return "login";
        }

        return "redirect:/login";
    }

    @GetMapping
    public String handleGet(@RequestParam(required = false) String action, HttpSession session) {
        if ("logout".equals(action)) {
            if (session != null) {
                session.invalidate();
            }
        }
        return "redirect:/books";
    }
}
