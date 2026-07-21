package com.example.bookplan.book.service;

import com.example.bookplan.book.Book;
import com.example.bookplan.book.BookRepository;
import com.example.bookplan.book.BookSearchType;
import com.example.bookplan.book.BookService;
import com.example.bookplan.book.dto.BookCreateRequest;
import com.example.bookplan.book.dto.BookUpdateRequest;
import com.example.bookplan.book.exception.NotFoundBookException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(BookService.class)
public class BookServiceTest {

    @Autowired
    BookService service;
    @Autowired
    BookRepository repository;

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long NON_EXISTENT_BOOK_ID = 999L;

    @Test
    @DisplayName("제목 검색은 대소문자를 구분하지 않는다")
    void searchTitleIgnoringCase() {
        repository.save(Book.from(USER_ID, "Clean Code", 464, "로버트 C. 마틴", null, null, null, null));

        List<Book> books = service.findAll(USER_ID, BookSearchType.TITLE, "clean");

        assertThat(books).extracting(Book::getTitle).containsExactly("Clean Code");
    }

    @Test
    @DisplayName("제목 일부만으로도 검색된다")
    void searchTitleByPartialMatch() {
        repository.save(Book.from(USER_ID, "여행의 이유", 208, "김영하", null, null, null, null));

        List<Book> books = service.findAll(USER_ID, BookSearchType.TITLE, "이유");

        assertThat(books).extracting(Book::getTitle).containsExactly("여행의 이유");
    }

    @Test
    @DisplayName("작가 일부만으로도 검색된다")
    void searchAuthorsByPartialMatch() {
        repository.save(Book.from(USER_ID, "Clean Code", 464, "로버트 C. 마틴", null, null, null, null));

        List<Book> books = service.findAll(USER_ID, BookSearchType.AUTHOR, "마틴");

        assertThat(books).extracting(Book::getAuthors).containsExactly("로버트 C. 마틴");
    }

    @Test
    @DisplayName("검색어가 없으면 해당 사용자의 전체 목록만 조회된다")
    void findAllWithoutKeywordReturnsUsersBooks() {
        repository.save(Book.from(USER_ID, "여행의 이유", 208, "김영하", null, null, null, null));
        repository.save(Book.from(USER_ID, "Clean Code", 464, "로버트 C. 마틴", null, null, null, null));
        repository.save(Book.from(OTHER_USER_ID, "다른 사람의 책", 100, "다른 저자", null, null, null, null));

        List<Book> books = service.findAll(USER_ID, null, null);

        assertThat(books).hasSize(2);
        assertThat(books).allMatch(book -> book.getUserId().equals(USER_ID));
    }

    @Test
    @DisplayName("책 단일 조회가 정상적으로 된다")
    void findBook() {
        Book saved = repository.save(Book.from(USER_ID, "테스트 책", 100, "홍길동",
                "테스트 번역가", "테스트 출판사", "9788956746425", "https://example.com/cover.jpg"));

        Book book = service.findOne(saved.getId(), USER_ID);

        assertThat(book.getUserId()).isEqualTo(USER_ID);
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
        assertThatThrownBy(() -> service.findOne(NON_EXISTENT_BOOK_ID, USER_ID))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining("존재하지 않는 책입니다: " + NON_EXISTENT_BOOK_ID);
    }

    @Test
    @DisplayName("책 등록이 정상적으로 된다")
    void createBook() {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("테스트 책")
                .totalPages(100)
                .authors("홍길동 외 2명")
                .translators("테스트 번역가")
                .publisher("테스트 출판사")
                .build();

        Book book = service.create(request, USER_ID);

        assertThat(book.getId()).isNotNull();
        assertThat(book.getUserId()).isEqualTo(USER_ID);
        assertThat(book.getTitle()).isEqualTo("테스트 책");
        assertThat(book.getTotalPages()).isEqualTo(100);
        assertThat(book.getAuthors()).isEqualTo("홍길동 외 2명");
        assertThat(book.getTranslators()).isEqualTo("테스트 번역가");
        assertThat(book.getPublisher()).isEqualTo("테스트 출판사");
    }

    @Test
    @DisplayName("책 수정이 정상적으로 된다")
    void updateBook() {
        Book saved = repository.save(Book.from(USER_ID, "기존 제목", 100, "기존 저자",
                "기존 번역가", "기존 출판사", "9788956746425", "https://example.com/old.jpg"));

        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("수정된 제목")
                .totalPages(200)
                .build();

        Book book = service.update(request, saved.getId(), USER_ID);

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
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("수정된 제목")
                .totalPages(200)
                .build();

        assertThatThrownBy(() -> service.update(request, NON_EXISTENT_BOOK_ID, USER_ID))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining("존재하지 않는 책입니다: " + NON_EXISTENT_BOOK_ID);
    }

    @Test
    @DisplayName("책이 정상적으로 삭제된다")
    void deleteBook() {
        Book saved = repository.save(Book.from(USER_ID, "삭제될 책", 100, "저자", null, null, null, null));

        long deletedBookCount = service.delete(saved.getId(), USER_ID);

        assertThat(deletedBookCount).isEqualTo(1);
        assertThat(repository.findByIdAndUserId(saved.getId(), USER_ID)).isEmpty();
    }

    @Test
    @DisplayName("책을 삭제하는데 책이 존재하지 않으면 404 에러가 발생한다")
    void deleteAndValidateBook() {
        assertThatThrownBy(() -> service.delete(NON_EXISTENT_BOOK_ID, USER_ID))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining("존재하지 않는 책입니다: " + NON_EXISTENT_BOOK_ID);
    }
}
