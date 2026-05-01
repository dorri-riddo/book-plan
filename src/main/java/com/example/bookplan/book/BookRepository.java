package com.example.bookplan.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIdAndUserId(Long bookId, Long userId);
    List<Book> findAllByUserId(Long userId);
    long deleteByIdAndUserId(Long bookId, Long userId);
}
