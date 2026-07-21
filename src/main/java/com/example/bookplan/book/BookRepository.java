package com.example.bookplan.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Optional<Book> findByIdAndUserId(Long bookId, Long userId);
    long deleteByIdAndUserId(Long bookId, Long userId);
}
