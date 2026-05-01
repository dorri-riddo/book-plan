package com.example.bookplan.readingGoal;

import com.example.bookplan.book.Book;
import com.example.bookplan.book.BookRepository;
import com.example.bookplan.book.exception.NotFoundBookException;
import com.example.bookplan.readingGoal.dto.*;
import com.example.bookplan.readingGoal.exception.InvalidCurrentPageException;
import com.example.bookplan.readingGoal.exception.NotFoundReadingGoalException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadingGoalService {
    private final ReadingGoalRepository repository;
    private final BookRepository bookRepository;
    private final Clock clock;

    public List<ReadingGoal> findAll(Long userId) {
        List<ReadingGoal> readingGoals = repository.findAllByUserId(userId);
        return readingGoals;
    }

    public ReadingGoal findOne(Long readingGoalId, Long userId) {
        ReadingGoal readingGoal = repository.findByIdAndUserId(readingGoalId, userId)
                .orElseThrow(() -> new NotFoundReadingGoalException(readingGoalId));

        return readingGoal;
    }

    public ReadingGoal create(ReadingGoalCreateRequest request, Long userId) {
        Long bookId = request.getBookId();
        bookRepository.findByIdAndUserId(bookId, userId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        ReadingGoal readingGoal = repository.save(
                ReadingGoal.from(userId, bookId, request.getTargetDate(), request.getTargetPage()));

        return readingGoal;
    }

    public ReadingGoal updateCurrentPage(ReadingGoalUpdateCurrentPageRequest request, Long readingGoalId, Long userId) {
        ReadingGoal readingGoal = repository.findByIdAndUserId(readingGoalId, userId)
                .orElseThrow(() -> new NotFoundReadingGoalException(readingGoalId));

        int currentPage = request.getCurrentPage();
        int targetPage = readingGoal.getTargetPage();
        if (currentPage > targetPage) {
            throw new InvalidCurrentPageException(readingGoalId);
        }

        if (currentPage == targetPage) {
            ReadingGoalUpdateStatusRequest statusRequest = ReadingGoalUpdateStatusRequest.builder()
                    .status(ReadingGoalStatus.COMPLETED)
                    .build();
            this.updateStatus(statusRequest, readingGoalId, userId);
        }

        readingGoal.updateCurrentPage(request.getCurrentPage());

        return readingGoal;
    }

    public ReadingGoal updateTargetDate(ReadingGoalUpdateTargetDateRequest request, Long readingGoalId, Long userId) {
        ReadingGoal readingGoal = repository.findByIdAndUserId(readingGoalId, userId)
                .orElseThrow(() -> new NotFoundReadingGoalException(readingGoalId));

        readingGoal.updateTargetDate(request.getTargetDate());

        return readingGoal;
    }

    public ReadingGoal updateStatus(ReadingGoalUpdateStatusRequest request, Long readingGoalId, Long userId) {
        ReadingGoal readingGoal = repository.findByIdAndUserId(readingGoalId, userId)
                .orElseThrow(() -> new NotFoundReadingGoalException(readingGoalId));

        readingGoal.updateStatus(request.getStatus());

        return readingGoal;
    }

    public long delete(Long readingGoalId, Long userId) {
        long deletedReadingGoalCount = repository.deleteByIdAndUserId(readingGoalId, userId);
        if (deletedReadingGoalCount == 0) {
            throw new NotFoundReadingGoalException(readingGoalId);
        }

        return deletedReadingGoalCount;
    }

    public List<ReadingGoalCalculatePagesPerDayResponse> calculatePagesPerDay(Long userId) {
        Instant now = Instant.now(clock);

        List<ReadingGoal> readingGoals = repository.findAllByUserIdAndStatus(userId, ReadingGoalStatus.IN_PROGRESS);
        List<Long> bookIds = readingGoals.stream().map(ReadingGoal::getBookId).toList();
        Map<Long, Book> bookMap = bookRepository.findAllById(bookIds).stream()
                .collect(toMap(Book::getId, identity()));

        return readingGoals.stream()
                .map(goal -> ReadingGoalCalculatePagesPerDayResponse.of(goal, bookMap.get(goal.getBookId()), now))
                .toList();
     }
}
