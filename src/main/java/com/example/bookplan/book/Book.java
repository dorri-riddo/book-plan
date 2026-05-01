package com.example.bookplan.book;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * 사용자가 자신의 환경(판본, 글자 크기 등)에 맞춰 등록한 책 인스턴스.
 * 동일 ISBN이라도 사용자/형태별로 다른 row를 가진다.
 * → totalPages가 사용자별로 다르기 때문 (특히 이북).
 */
@Entity
@Table(name = "books")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false)
    private int totalPages;

    @Column(nullable = false, length = 200)
    private String authors;

    @Column(nullable = true, length = 200)
    private String translators;

    @Column(nullable = true, length = 500)
    private String publisher;

    @Column(nullable = true, length = 13)
    private String isbn;

    @Column(nullable = true, length = 500)
    private String coverImageUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    protected Book() {}

    private Book(Long userId, String title, int totalPages, String authors, String translators
            , String publisher, String isbn, String coverImageUrl) {
        this.userId = userId;
        this.title = title;
        this.totalPages = totalPages;
        this.authors = authors;
        this.translators = translators;
        this.publisher = publisher;
        this.isbn = isbn;
        this.coverImageUrl = coverImageUrl;
    }

    public void update(String title, Integer totalPages, String authors,
                       String translators, String publisher, String isbn, String coverImageUrl) {
        if (title != null) {
            this.title = title;
        }
        if (totalPages != null) {
            this.totalPages = totalPages;
        }
        if (authors != null) {
            this.authors = authors;
        }
        if (translators != null) {
            this.translators = translators;
        }
        if (publisher != null) {
            this.publisher = publisher;
        }
        if (isbn != null) {
            this.isbn = isbn;
        }
        if (coverImageUrl != null) {
            this.coverImageUrl = coverImageUrl;
        }
    }

    public static Book from(Long userId, String title, int totalPages, String authors,
                            String translators, String publisher, String isbn, String coverImageUrl) {
        return new Book(userId, title, totalPages, authors, translators, publisher, isbn, coverImageUrl);
    }
}
