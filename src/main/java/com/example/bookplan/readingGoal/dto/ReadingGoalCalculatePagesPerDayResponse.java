package com.example.bookplan.readingGoal.dto;

import com.example.bookplan.book.Book;
import com.example.bookplan.readingGoal.ReadingGoal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@Schema(description = "일일 목표 페이지 계산 응답")
public class ReadingGoalCalculatePagesPerDayResponse {
    @Schema(description = "독서 목표 ID", example = "1")
    private Long id;

    @Schema(description = "책 제목", example = "클린 코드")
    private String title;

    @Schema(description = "현재 읽은 페이지", example = "150")
    private Integer currentPage;

    @Schema(description = "목표 페이지", example = "464")
    private Integer targetPage;

    @Schema(description = "남은 일수", example = "14")
    private Integer remainingDay;

    @Schema(description = "오늘 읽어야 할 페이지 수", example = "23")
    private Integer todayTargetPage;

    @Schema(description = "진행률 (%)", example = "32")
    private Integer percent;

    @Schema(description = "목표 완료 날짜", example = "2026-06-30T00:00:00Z")
    private Instant targetDate;

    protected ReadingGoalCalculatePagesPerDayResponse() {}

    private ReadingGoalCalculatePagesPerDayResponse(Long id, String title, Integer currentPage, Integer targetPage,
                                                    Integer remainingDay, Integer todayTargetPage, Integer percent, Instant targetDate) {
        this.id = id;
        this.title = title;
        this.currentPage = currentPage;
        this.targetPage = targetPage;
        this.remainingDay = remainingDay;
        this.todayTargetPage = todayTargetPage;
        this.percent = percent;
        this.targetDate = targetDate;
    }

    public static ReadingGoalCalculatePagesPerDayResponse of(ReadingGoal readingGoal, Book book, Instant now) {
        int remainingDay = readingGoal.calculateRemainingDay(now);
        return new ReadingGoalCalculatePagesPerDayResponse(
                readingGoal.getId(),
                book.getTitle(),
                readingGoal.getCurrentPage(),
                readingGoal.getTargetPage(),
                remainingDay,
                readingGoal.calculateTodayTargetPage(remainingDay),
                readingGoal.calculatePercent(),
                readingGoal.getTargetDate()
        );
    }
}
