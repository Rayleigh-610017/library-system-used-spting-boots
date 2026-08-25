package com.example.library.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.library.entity.Account;
import com.example.library.entity.ReadingLog;
import com.example.library.repository.ReadingLogRepository;
import com.example.library.service.BookService;

@Controller
@RequestMapping("/rent")
public class RentController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReadingLogRepository readingLogRepository;

    // 貸出ログ画面の表示
    @GetMapping
    public String showRentLog(HttpSession session, Model model) {
        Account user = (session != null) ? (Account) session.getAttribute("loginUser") : null;
        if (user == null) {
            return "redirect:/login";
        }

        List<ReadingLog> logs;
        if ("Admin".equals(user.getRole())) {
            logs = readingLogRepository.findAllByOrderByRentDateDesc();
            model.addAttribute("isAdminView", true);
        } else {
            logs = readingLogRepository.findByUserIdOrderByRentDateDesc(user.getUserId());
            model.addAttribute("isAdminView", false);
        }

        model.addAttribute("logs", logs);
        return "reading_log";
    }

    // 貸出・返却処理の受付
    @PostMapping
    public String handleRent(@RequestParam String action,
                             @RequestParam String isbn,
                             HttpSession session) {
        Account user = (session != null) ? (Account) session.getAttribute("loginUser") : null;
        if (user == null) {
            return "redirect:/login";
        }

        session.removeAttribute("rentError");

        if ("rent".equals(action)) {
            boolean success = bookService.rentBook(user.getUserId(), isbn);
            if (!success) {
                session.setAttribute("rentError", "この書籍は現在貸出中または存在しません。");
            }
        } else if ("return".equals(action)) {
            // 管理者権限かどうか判定
            boolean isAdmin = "Admin".equals(user.getRole());

            // ユーザーIDと管理者フラグを BookService に渡して本人チェックを実行
            boolean success = bookService.returnBook(user.getUserId(), isbn, isAdmin);
            if (!success) {
                session.setAttribute("rentError", "自分が貸出中の本しか返却できません。");
            }
        }

        return "redirect:/books";
    }
}