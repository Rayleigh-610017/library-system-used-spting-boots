package com.example.library.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.library.entity.Book;
import com.example.library.entity.ReadingLog;
import com.example.library.repository.BookRepository;
import com.example.library.repository.ReadingLogRepository;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReadingLogRepository readingLogRepository;

    /**
     * 貸出処理
     */
    @Transactional
    public boolean rentBook(Integer userId, String isbn) {
        Book book = bookRepository.findById(isbn).orElse(null);

        if (book == null || "Rent".equals(book.getRentStatus())) {
            return false;
        }

        ReadingLog log = new ReadingLog();
        log.setUserId(userId);
        log.setIsbn(isbn);
        log.setRentDate(LocalDateTime.now());
        readingLogRepository.save(log);

        book.setRentStatus("Rent");
        bookRepository.save(book);

        return true;
    }

    /**
     * 返却処理（本人チェック機能付き）
     * @param userId ログイン中のユーザーID
     * @param isbn 返却対象のISBN
     * @param isAdmin 管理者権限かどうか
     * @return 返却成功時 true、本人以外またはエラー時 false
     */
    @Transactional
    public boolean returnBook(Integer userId, String isbn, boolean isAdmin) {
        Book book = bookRepository.findById(isbn).orElse(null);
        if (book == null || !"Rent".equals(book.getRentStatus())) {
            return false; // 本が存在しない、または貸出中ではない
        }

        // 管理者でない場合は、最新の貸出ログを調べて本人かチェックする
        if (!isAdmin) {
            List<ReadingLog> logs = readingLogRepository.findByIsbnOrderByRentDateDesc(isbn);
            if (!logs.isEmpty()) {
                ReadingLog latestLog = logs.get(0);
                // 最新の貸出ログのユーザーIDと、現在ログインしているユーザーIDが一致しない場合は拒否
                if (!latestLog.getUserId().equals(userId)) {
                    return false;
                }
            }
        }

        // 返却処理：ステータスを 'Available' に戻す
        book.setRentStatus("Available");
        bookRepository.save(book);
        return true;
    }

    /**
     * 検索条件に合った書籍一覧を取得し、貸出中の本については最新の借用者IDをセットして返す
     */
    public List<Book> getBooksWithBorrower(String keyword, String category) {
        List<Book> books = bookRepository.searchBooks(keyword, category);
        
        for (Book book : books) {
            if ("Rent".equals(book.getRentStatus())) {
                List<ReadingLog> logs = readingLogRepository.findByIsbnOrderByRentDateDesc(book.getIsbn());
                if (!logs.isEmpty()) {
                    // 最新の貸出ログから、現在借りているユーザーのIDをセット
                    book.setBorrowerUserId(logs.get(0).getUserId());
                }
            }
        }
        return books;
    }

    /**
     * 借用者情報を含む書籍リストをページネーション（指定件数ごと）で取得する
     */
    public Page<Book> getBooksWithBorrowerPage(String keyword, String category, Pageable pageable) {
        // 1. 既存のロジックで条件に合う全ての書籍（借用者情報補完済み）を取得
        List<Book> allBooks = getBooksWithBorrower(keyword, category);

        // 2. 取得結果のインデックス範囲（30件分）を計算
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allBooks.size());

        // リストが空、または開始位置が全体の件数を超えている場合の処理
        if (start >= allBooks.size()) {
            return new PageImpl<>(List.of(), pageable, allBooks.size());
        }

        // 3. 30件分に切り出したサブリストを取得し、PageImpl オブジェクトとして返却
        List<Book> pageContent = allBooks.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allBooks.size());
    }
}