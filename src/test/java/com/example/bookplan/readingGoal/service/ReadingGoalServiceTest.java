package com.example.bookplan.readingGoal.service;

import com.example.bookplan.book.Book;
import com.example.bookplan.book.BookRepository;
import com.example.bookplan.book.exception.NotFoundBookException;
import com.example.bookplan.readingGoal.ReadingGoal;
import com.example.bookplan.readingGoal.ReadingGoalRepository;
import com.example.bookplan.readingGoal.ReadingGoalService;
import com.example.bookplan.readingGoal.ReadingGoalStatus;
import com.example.bookplan.readingGoal.dto.*;
import com.example.bookplan.readingGoal.exception.InvalidCurrentPageException;
import com.example.bookplan.readingGoal.exception.NotFoundReadingGoalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadingGoalServiceTest {
    @Mock
    ReadingGoalRepository readingGoalRepository;
    @Mock
    BookRepository bookRepository;
    @InjectMocks
    ReadingGoalService service;

    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2026-05-05T00:00:00.000Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    void setUp() {
        service = new ReadingGoalService(readingGoalRepository, bookRepository, fixedClock);
    }

    @Test
    @DisplayName("목표 목록이 정상적으로 조회된다")
    void findBooks() {
        Long userId = 1L;

        ReadingGoal readingGoal1 = ReadingGoal
                .from(userId, 1L, Instant.parse("2026-05-10T15:00:00.000Z"), 100);
        ReadingGoal readingGoal2 = ReadingGoal.from(userId, 2L, Instant.parse("2026-05-15T15:00:00.000Z"), 300);

        when(readingGoalRepository.findAllByUserId(userId))
                .thenReturn(List.of(readingGoal1, readingGoal2));

        List<ReadingGoal> readingGoals = service.findAll(userId);

        assertThat(readingGoals).hasSize(2);
        assertThat(readingGoals)
                .extracting(ReadingGoal::getBookId)
                .containsExactly(1L, 2L);
        assertThat(readingGoals).allMatch(book -> book.getUserId().equals(userId));
    }

    @Test
    @DisplayName("목표 단일 조회가 정상적으로 된 다")
    void findReadingGoal() {
        Long readingGoalId = 1L;
        Long bookId = 1L;
        Long userId = 1L;

        ReadingGoal existing = ReadingGoal.from(userId, bookId, Instant.parse("2026-05-10T15:00:00.000Z"), 300);

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.of(existing));

        ReadingGoal readingGoal = service.findOne(readingGoalId, userId);

        assertThat(readingGoal.getBookId()).isEqualTo(bookId);
        assertThat(readingGoal.getUserId()).isEqualTo(userId);
        assertThat(readingGoal.getCurrentPage()).isEqualTo(0);
        assertThat(readingGoal.getTargetDate()).isEqualTo(Instant.parse("2026-05-10T15:00:00.000Z"));
        assertThat(readingGoal.getStatus()).isEqualTo(ReadingGoalStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("목표 단일 조회 하는데 목표가 존재하지 않으면 404 에러가 발생한다")
    void findAndValidateReadingGoal() {
        Long readingGoalId = 1L;
        Long userId = 1L;

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findOne(readingGoalId, userId))
                .isInstanceOf(NotFoundReadingGoalException.class)
                .hasMessageContaining("존재하지 않는 목표입니다: 1");
    }

    @Test
    @DisplayName("목표 등록이 정상적으로 된다")
    void createReadingGoal() {
        Long bookId = 1L;
        Long userId = 1L;
        ReadingGoalCreateRequest request = ReadingGoalCreateRequest.builder()
                .bookId(bookId)
                .targetDate(Instant.parse("2026-05-10T15:00:00.000Z"))
                .targetPage(300)
                .build();
        Book existing = Book.from(userId, "테스트 책", 100, "홍길동",
                "테스트 번역가", "테스트 출판사", "9788956746425", "https://example.com/cover.jpg");

        when(bookRepository.findByIdAndUserId(bookId, userId))
                .thenReturn(Optional.of(existing));
        when(readingGoalRepository.save(any(ReadingGoal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        ReadingGoal readingGoal = service.create(request, userId);

        assertThat(readingGoal.getBookId()).isEqualTo(bookId);
        assertThat(readingGoal.getUserId()).isEqualTo(userId);
        assertThat(readingGoal.getCurrentPage()).isEqualTo(0);
        assertThat(readingGoal.getTargetDate()).isEqualTo(Instant.parse("2026-05-10T15:00:00.000Z"));
        assertThat(readingGoal.getStatus()).isEqualTo(ReadingGoalStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("목표 등록을 하는데 책이 등록되지 않으면 404 에러를 응답한다")
    void createAndValidateReadingGoal() {
        Long bookId = 1L;
        Long userId = 1L;
        ReadingGoalCreateRequest request = ReadingGoalCreateRequest.builder()
                .bookId(bookId)
                .targetDate(Instant.parse("2026-05-10T15:00:00.000Z"))
                .targetPage(300)
                .build();

        when(bookRepository.findByIdAndUserId(bookId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request, userId))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining("존재하지 않는 책입니다: 1");
    }

    @Test
    @DisplayName("현재 읽은 분량을 변경한다")
    void updateReadingGoalCurrentPage() {
        Long readingGoalId = 1L;
        Long bookId = 1L;
        Long userId = 1L;

        ReadingGoal existing = ReadingGoal.from(userId, bookId, Instant.parse("2026-05-10T15:00:00.000Z"), 300);

        ReadingGoalUpdateCurrentPageRequest request = ReadingGoalUpdateCurrentPageRequest.builder()
                        .currentPage(200)
                        .build();

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.of(existing));

        ReadingGoal readingGoal = service.updateCurrentPage(request, readingGoalId, userId);

        assertThat(readingGoal.getCurrentPage()).isEqualTo(200);
    }

    @Test
    @DisplayName("현재 읽은 분량을 변경하는데 목표 분량과 일치하면 완료로 변경한다")
    void updateReadingGoalCurrentPageAndStatus() {
        Long readingGoalId = 1L;
        Long bookId = 1L;
        Long userId = 1L;

        ReadingGoal existing = ReadingGoal.from(userId, bookId, Instant.parse("2026-05-10T15:00:00.000Z"), 200);

        ReadingGoalUpdateCurrentPageRequest request = ReadingGoalUpdateCurrentPageRequest.builder()
                .currentPage(200)
                .build();

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.of(existing));

        ReadingGoal readingGoal = service.updateCurrentPage(request, readingGoalId, userId);

        assertThat(readingGoal.getCurrentPage()).isEqualTo(200);
        assertThat(readingGoal.getStatus()).isEqualTo(ReadingGoalStatus.COMPLETED);
    }

    @Test
    @DisplayName("현재 읽은 분량을 변경하는데 목표가 존재하지 않으면 404 에러가 발생한다")
    void updateAndValidateReadingGoalCurrentPage() {
        Long readingGoalId = 1L;
        Long userId = 1L;

        ReadingGoalUpdateCurrentPageRequest request = ReadingGoalUpdateCurrentPageRequest.builder()
                .currentPage(200)
                .build();

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateCurrentPage(request, readingGoalId, userId))
                .isInstanceOf(NotFoundReadingGoalException.class)
                .hasMessageContaining("존재하지 않는 목표입니다: 1");
    }

    @Test
    @DisplayName("현재 읽은 분량을 변경하는데 목표가 책의 분량보다 넘으면 400 에러가 발생한다")
    void validateReadingGoalCurrentPageInvalidCurrentPageException() {
        Long readingGoalId = 1L;
        Long bookId = 1L;
        Long userId = 1L;

        ReadingGoal existing = ReadingGoal.from(userId, bookId, Instant.parse("2026-05-10T15:00:00.000Z"), 300);

        ReadingGoalUpdateCurrentPageRequest request = ReadingGoalUpdateCurrentPageRequest.builder()
                .currentPage(301)
                .build();

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.updateCurrentPage(request, readingGoalId, userId))
                .isInstanceOf(InvalidCurrentPageException.class)
                .hasMessageContaining("책 페이지보다 읽은 페이지가 더 많습니다: 1");
    }

    @Test
    @DisplayName("목표 날짜를 변경한다")
    void updateReadingGoalTargetDate() {
        Long readingGoalId = 1L;
        Long bookId = 1L;
        Long userId = 1L;

        ReadingGoal existing = ReadingGoal.from(userId, bookId, Instant.parse("2026-05-10T15:00:00.000Z"), 300);

        ReadingGoalUpdateTargetDateRequest request = ReadingGoalUpdateTargetDateRequest.builder()
                .targetDate(Instant.parse("2026-05-15T15:00:00.000Z"))
                .build();

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.of(existing));

        ReadingGoal readingGoal = service.updateTargetDate(request, readingGoalId, userId);

        assertThat(readingGoal.getTargetDate()).isEqualTo(Instant.parse("2026-05-15T15:00:00.000Z"));
    }


    @Test
    @DisplayName("목표 날짜를 변경하는데 목표가 존재하지 않으면 404 에러가 발생한다")
    void updateAndValidateReadingGoalTargetDate() {
        Long readingGoalId = 1L;
        Long userId = 1L;

        ReadingGoalUpdateTargetDateRequest request = ReadingGoalUpdateTargetDateRequest.builder()
                .targetDate(Instant.parse("2026-05-15T15:00:00.000Z"))
                .build();

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTargetDate(request, readingGoalId, userId))
                .isInstanceOf(NotFoundReadingGoalException.class)
                .hasMessageContaining("존재하지 않는 목표입니다: 1");
    }

    @Test
    @DisplayName("목표 상태를 변경한다")
    void updateReadingGoalStatus() {
        Long readingGoalId = 1L;
        Long bookId = 1L;
        Long userId = 1L;

        ReadingGoal existing = ReadingGoal.from(userId, bookId, Instant.parse("2026-05-10T15:00:00.000Z"), 300);

        ReadingGoalUpdateStatusRequest request = ReadingGoalUpdateStatusRequest.builder()
                .status(ReadingGoalStatus.COMPLETED)
                .build();

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.of(existing));

        ReadingGoal readingGoal = service.updateStatus(request, readingGoalId, userId);

        assertThat(readingGoal.getStatus()).isEqualTo(ReadingGoalStatus.COMPLETED);
    }


    @Test
    @DisplayName("목표 상태를 변경하는데 목표가 존재하지 않으면 404 에러가 발생한다")
    void updateAndValidateReadingGoalStatus() {
        Long readingGoalId = 1L;
        Long userId = 1L;

        ReadingGoalUpdateStatusRequest request = ReadingGoalUpdateStatusRequest.builder()
                .status(ReadingGoalStatus.COMPLETED)
                .build();

        when(readingGoalRepository.findByIdAndUserId(readingGoalId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(request, readingGoalId, userId))
                .isInstanceOf(NotFoundReadingGoalException.class)
                .hasMessageContaining("존재하지 않는 목표입니다: 1");
    }


    @Test
    @DisplayName("목표가 정상적으로 삭제된다")
    void deleteReadingGoal() {
        Long readingGoalId = 1L;
        Long userId = 1L;

        when(readingGoalRepository.deleteByIdAndUserId(readingGoalId, userId))
                .thenReturn(1L);

        Long deletedReadingGoalCount = service.delete(readingGoalId, userId);

        verify(readingGoalRepository, times(1)).deleteByIdAndUserId(readingGoalId, userId);
        assertThat(deletedReadingGoalCount).isEqualTo(1);
    }

    @Test
    @DisplayName("목표가 삭제하는데 목표가 존재하지 않으면 404 에러가 발생한다")
    void deleteAndValidateReadingGoal() {
        Long readingGoalId = 1L;
        Long userId = 1L;

        when(readingGoalRepository.deleteByIdAndUserId(readingGoalId, userId))
                .thenReturn(0L);

        assertThatThrownBy(() -> service.delete(readingGoalId, userId))
                .isInstanceOf(NotFoundReadingGoalException.class)
                .hasMessageContaining("존재하지 않는 목표입니다: 1");
    }

    @Test
    @DisplayName("오늘의 목표치를 응답한다")
    void calculatePagesPerDay() {
        Long userId = 1L;

        Book book1 = Book.from(userId, "첫 번째 책", 200, "저자1", null, null, null, null);
        Book book2 = Book.from(userId, "두 번째 책", 300, "저자2", null, null, null, null);
        ReflectionTestUtils.setField(book1, "id", 1L);
        ReflectionTestUtils.setField(book2, "id", 2L);
        when(bookRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(book1, book2));
        ReadingGoal readingGoal1 = ReadingGoal
                .from(userId, 1L, Instant.parse("2026-05-10T00:00:00.000Z"), 200);
        ReadingGoal readingGoal2 = ReadingGoal.from(userId, 2L, Instant.parse("2026-05-15T00:00:00.000Z"), 300);
        when(readingGoalRepository.findAllByUserIdAndStatus(userId, ReadingGoalStatus.IN_PROGRESS))
                .thenReturn(List.of(readingGoal1, readingGoal2));

        List<ReadingGoalCalculatePagesPerDayResponse> list = service.calculatePagesPerDay(userId);

        assertThat(list).hasSize(2);
        assertThat(list)
                .extracting(ReadingGoalCalculatePagesPerDayResponse::getTitle)
                .containsExactly("첫 번째 책", "두 번째 책");
        assertThat(list)
                .extracting(ReadingGoalCalculatePagesPerDayResponse::getTodayTargetPage)
                .containsExactly(40, 30);
    }
}
