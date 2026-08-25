package com.example.library.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.library.entity.Account;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    // 書籍一覧画面の表示（ページネーション対応：1ページ30件）
    @GetMapping
    public String getBooks(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String category,
                           @RequestParam(required = false) String view,
                           @RequestParam(defaultValue = "0") int page, // 追加：ページ番号（0始まり）
                           HttpSession session,
                           Model model) {

        Account user = (session != null) ? (Account) session.getAttribute("loginUser") : null;

        // 1ページあたり30件を指定
        Pageable pageable = PageRequest.of(page, 30);

        // Service から 30件毎に取得された Page<Book> を取得
        Page<Book> bookPage = bookService.getBooksWithBorrowerPage(keyword, category, pageable);
        List<String> categories = bookRepository.findDistinctCategories();

        // テンプレート（Thymeleaf）に配る属性を設定
        model.addAttribute("bookPage", bookPage);
        model.addAttribute("books", bookPage.getContent()); // 既存のリスト表示との互換用
        model.addAttribute("categories", categories);

        if ("admin".equals(view) && user != null && "Admin".equals(user.getRole())) {
            return "admin_book_manage";
        }
        return "book_list";
    }

    // 書籍管理アクション（追加・更新・削除・強制返却）の処理
    @PostMapping
    public String postBooks(@RequestParam String action,
                            @RequestParam(required = false) String isbn,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String writer,
                            @RequestParam(required = false) String publisher,
                            @RequestParam(required = false) String url,
                            @RequestParam(required = false) String view,
                            HttpSession session) {

        Account user = (session != null) ? (Account) session.getAttribute("loginUser") : null;

        if (user == null) {
            return "redirect:/login";
        }

        // 管理者権限が必要なアクションの保護
        if ("add".equals(action) || "update".equals(action) || "delete".equals(action) || "forceReturn".equals(action)) {
            if (!"Admin".equals(user.getRole())) {
                return "redirect:/books";
            }
        }

        if ("add".equals(action)) {
            Book book = new Book(isbn, title, category, writer, publisher, url, "Available", null);
            bookRepository.save(book);
        } else if ("update".equals(action)) {
            Book book = bookRepository.findById(isbn).orElse(new Book());
            book.setTitle(title);
            book.setCategory(category);
            book.setWriter(writer);
            book.setPublisher(publisher);
            book.setUrl(url);
            bookRepository.save(book);
        } else if ("delete".equals(action)) {
            bookRepository.deleteById(isbn);
        } else if ("forceReturn".equals(action)) {
            // 管理者による強制返却（isAdmin = true を指定）
            bookService.returnBook(user.getUserId(), isbn, true);
        }

        if ("admin".equals(view) && "Admin".equals(user.getRole())) {
            return "redirect:/books?view=admin";
        }
        return "redirect:/books";
    }
}