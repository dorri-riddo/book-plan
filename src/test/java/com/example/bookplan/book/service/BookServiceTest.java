package com.example.bookplan.book.service;

import com.example.bookplan.book.Book;
import com.example.bookplan.book.BookRepository;
import com.example.bookplan.book.BookService;
import com.example.bookplan.book.dto.BookCreateRequest;
import com.example.bookplan.book.dto.BookUpdateRequest;
import com.example.bookplan.book.exception.NotFoundBookException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    BookRepository bookRepository;
    @InjectMocks
    BookService service;

    @Test
    @DisplayName("책 목록이 정상적으로 조회된다")
    void findBooks() {
        Long userId = 1L;

        Book book1 = Book.from(userId, "첫 번째 책", 100, "저자1", null, null, null, null);
        Book book2 = Book.from(userId, "두 번째 책", 200, "저자2", null, null, null, null);

        when(bookRepository.findAllByUserId(userId))
                .thenReturn(List.of(book1, book2));

        List<Book> books = service.findAll(userId);

        assertThat(books).hasSize(2);
        assertThat(books)
                .extracting(Book::getTitle)
                .containsExactly("첫 번째 책", "두 번째 책");
        assertThat(books).allMatch(book -> book.getUserId().equals(userId));
    }

    @Test
    @DisplayName("책 단일 조회가 정상적으로 된다")
    void findBook() {
        Long bookId = 1L;
        Long userId = 1L;

        Book existing = Book.from(userId, "테스트 책", 100, "홍길동",
                "테스트 번역가", "테스트 출판사", "9788956746425", "https://example.com/cover.jpg");

        when(bookRepository.findByIdAndUserId(bookId, userId))
                .thenReturn(Optional.of(existing));

        Book book = service.findOne(bookId, userId);

        assertThat(book.getUserId()).isEqualTo(userId);
        assertThat(book.getTitle()).isEqualTo("테스트 책");
        assertThat(book.getTotalPages()).isEqualTo(100);
        assertThat(book.getAuthors()).isEqualTo("홍길동");
        assertThat(book.getTranslators()).isEqualTo("테스트 번역가");
        assertThat(book.getPublisher()).isEqualTo("테스트 출판사");
        assertThat(book.getIsbn()).isEqualTo("9788956746425");
        assertThat(book.getCoverImageUrl()).isEqualTo("https://example.com/cover.jpg");
    }

    @Test
    @DisplayName("책 단일 조회 하는데 책이 존재하지 않으면 404 에러가 발생한다")
    void findAndValidateBook() {
        Long bookId = 1L;
        Long userId = 1L;

        when(bookRepository.findByIdAndUserId(bookId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findOne(bookId, userId))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining("존재하지 않는 책입니다: 1");
    }

    @Test
    @DisplayName("책 등록이 정상적으로 된다")
    void createBook() {
        Long userId = 1L;
        BookCreateRequest request = BookCreateRequest.builder()
                .title("테스트 책")
                .totalPages(100)
                .authors("홍길동 외 2명")
                .translators("테스트 번역가")
                .publisher("테스트 출판사")
                .build();

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book book = service.create(request, userId);

        assertThat(book.getUserId()).isEqualTo(userId);
        assertThat(book.getTitle()).isEqualTo("테스트 책");
        assertThat(book.getTotalPages()).isEqualTo(100);
        assertThat(book.getAuthors()).isEqualTo("홍길동 외 2명");
        assertThat(book.getTranslators()).isEqualTo("테스트 번역가");
        assertThat(book.getPublisher()).isEqualTo("테스트 출판사");
    }

    @Test
    @DisplayName("책 수정이 정상적으로 된다")
    void updateBook() {
        Long bookId = 1L;
        Long userId = 1L;

        Book existing = Book.from(userId, "기존 제목", 100, "기존 저자",
                "기존 번역가", "기존 출판사", "9788956746425", "https://example.com/old.jpg");

        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("수정된 제목")
                .totalPages(200)
                .build();

        when(bookRepository.findByIdAndUserId(bookId, userId)).thenReturn(Optional.of(existing));

        Book book = service.update(request, bookId, userId);

        // 요청에 포함된 필드는 변경됨
        assertThat(book.getTitle()).isEqualTo("수정된 제목");
        assertThat(book.getTotalPages()).isEqualTo(200);
        // 요청에 없던 필드는 그대로
        assertThat(book.getAuthors()).isEqualTo("기존 저자");
        assertThat(book.getTranslators()).isEqualTo("기존 번역가");
        assertThat(book.getPublisher()).isEqualTo("기존 출판사");
        assertThat(book.getIsbn()).isEqualTo("9788956746425");
        assertThat(book.getCoverImageUrl()).isEqualTo("https://example.com/old.jpg");
    }

    @Test
    @DisplayName("책을 수정하는데 책이 존재하지 않으면 404 에러가 발생한다")
    void validateBook() {
        Long bookId = 1L;
        Long userId = 1L;

        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("수정된 제목")
                .totalPages(200)
                .build();

        when(bookRepository.findByIdAndUserId(bookId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(request, bookId, userId))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining("존재하지 않는 책입니다: 1");
    }

    @Test
    @DisplayName("책이 정상적으로 삭제된다")
    void deleteBook() {
        Long bookId = 1L;
        Long userId = 1L;

        when(bookRepository.deleteByIdAndUserId(bookId, userId))
                .thenReturn(1L);

        Long deletedBookCount = service.delete(bookId, userId);

        verify(bookRepository, times(1)).deleteByIdAndUserId(bookId, userId);
        assertThat(deletedBookCount).isEqualTo(1);
    }

    @Test
    @DisplayName("책을 삭제하는데 책이 존재하지 않으면 404 에러가 발생한다")
    void deleteAndValidateBook() {
        Long bookId = 1L;
        Long userId = 1L;

        when(bookRepository.deleteByIdAndUserId(bookId, userId))
                .thenReturn(0L);

        assertThatThrownBy(() -> service.delete(bookId, userId))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining("존재하지 않는 책입니다: 1");
    }
}
