package com.example.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "Book")
public class Book {

    @Id
    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Category")
    private String category;

    @Column(name = "Writer")
    private String writer;

    @Column(name = "Publisher")
    private String publisher;

    @Column(name = "URL")
    private String url;

    @Column(name = "Rent_Status")
    private String rentStatus;

    // DBのテーブルには保存しない、一時的な借用者ID保持用フィールド
    @Transient
    private Integer borrowerUserId;

    // JPA用のデフォルトコンストラクタ
    public Book() {
    }

    // 全フィールド用コンストラクタ（通常）
    public Book(String isbn, String title, String category, String writer, String publisher, String url, String rentStatus) {
        this.isbn = isbn;
        this.title = title;
        this.category = category;
        this.writer = writer;
        this.publisher = publisher;
        this.url = url;
        this.rentStatus = rentStatus;
    }

    // 借用者ID含むコンストラクタ
    public Book(String isbn, String title, String category, String writer, String publisher, String url, String rentStatus, Integer borrowerUserId) {
        this.isbn = isbn;
        this.title = title;
        this.category = category;
        this.writer = writer;
        this.publisher = publisher;
        this.url = url;
        this.rentStatus = rentStatus;
        this.borrowerUserId = borrowerUserId;
    }

    // ゲッター・セッター
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRentStatus() {
        return rentStatus;
    }

    public void setRentStatus(String rentStatus) {
        this.rentStatus = rentStatus;
    }

    public Integer getBorrowerUserId() {
        return borrowerUserId;
    }

    public void setBorrowerUserId(Integer borrowerUserId) {
        this.borrowerUserId = borrowerUserId;
    }
}