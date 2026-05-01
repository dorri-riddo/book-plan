package com.example.bookplan.book;

import com.example.bookplan.book.dto.BookCreateRequest;
import com.example.bookplan.book.dto.BookUpdateRequest;
import com.example.bookplan.book.exception.NotFoundBookException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository repository;

    public List<Book> findAll(Long userId) {
        List<Book> books = repository.findAllByUserId(userId);
        return books;
    }

    public Book findOne(Long bookId, Long userId) {
        Book book = repository.findByIdAndUserId(bookId, userId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        return book;
    }

    public Book create(BookCreateRequest request, Long userId) {
        Book book = repository.save(Book.from(
                userId,
                request.getTitle(),
                request.getTotalPages(),
                request.getAuthors(),
                request.getTranslators(),
                request.getPublisher(),
                request.getIsbn(),
                request.getCoverImageUrl()));

        return book;
    }

    public Book update(BookUpdateRequest request, Long bookId, Long userId) {
        Book book = repository.findByIdAndUserId(bookId, userId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        book.update(
                request.getTitle(),
                request.getTotalPages(),
                request.getAuthors(),
                request.getTranslators(),
                request.getPublisher(),
                request.getIsbn(),
                request.getCoverImageUrl());

        return book;
    }

    public long delete(Long bookId, Long userId) {
        long deletedBookCount = repository.deleteByIdAndUserId(bookId, userId);
        if (deletedBookCount == 0) {
            throw new NotFoundBookException(bookId);
        }

        return deletedBookCount;
    }
}
