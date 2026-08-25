package com.example.library.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "Reading_Log")
@Data
public class ReadingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Log_ID")
    private Integer logId;

    @Column(name = "User_ID", nullable = false)
    private Integer userId;

    @Column(name = "ISBN", nullable = false)
    private String isbn;

    @Column(name = "Rent_Date")
    private LocalDateTime rentDate;

    // 書籍タイトル取得用リレーション
    @ManyToOne
    @JoinColumn(name = "ISBN", referencedColumnName = "ISBN", insertable = false, updatable = false)
    private Book book;
}