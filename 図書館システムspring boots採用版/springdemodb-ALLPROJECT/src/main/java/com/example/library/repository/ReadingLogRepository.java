package com.example.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.library.entity.ReadingLog;

@Repository
public interface ReadingLogRepository extends JpaRepository<ReadingLog, Integer> {

    List<ReadingLog> findByUserIdOrderByRentDateDesc(Integer userId);

    List<ReadingLog> findAllByOrderByRentDateDesc();

    // ★ 追加：特定ISBNの貸出履歴を新しい順で取得する
    List<ReadingLog> findByIsbnOrderByRentDateDesc(String isbn);
}