package com.example.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.library.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.category IS NOT NULL ORDER BY b.category")
    List<String> findDistinctCategories();

    @Query("SELECT b FROM Book b WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " b.isbn LIKE %:keyword% OR b.title LIKE %:keyword% OR " +
           " b.category LIKE %:keyword% OR b.writer LIKE %:keyword%) AND " +
           "(:category IS NULL OR :category = '' OR :category = 'all' OR b.category = :category)")
    List<Book> searchBooks(@Param("keyword") String keyword, @Param("category") String category);
}