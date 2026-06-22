package com.example.bookplan.readingGoal;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "readingGoals")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ReadingGoal {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private Instant targetDate;

    @Column(nullable = false)
    private int targetPage;

    @Column(nullable = false)
    private int currentPage;

    @Column(nullable = false)
    private int todayReadPages;

    @Column(nullable = false)
    private Instant currentPageUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReadingGoalStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    protected ReadingGoal() {}

    private ReadingGoal(Long userId, Long bookId, Instant targetDate,
                        int targetPage, int currentPage, ReadingGoalStatus status) {
        this.userId = userId;
        this.bookId = bookId;
        this.targetDate = targetDate;
        this.targetPage = targetPage;
        this.currentPage = currentPage;
        this.todayReadPages = 0;
        this.currentPageUpdatedAt = Instant.now();
        this.status = status;
    }

    public void updateCurrentPage(int newCurrentPage, Instant now) {
        int pageIncrement = newCurrentPage - this.currentPage;

        if (isSameDay(this.currentPageUpdatedAt, now)) {
            this.todayReadPages += pageIncrement;
        } else {
            this.todayReadPages = pageIncrement;
        }

        this.currentPageUpdatedAt = now;
        this.currentPage = newCurrentPage;
    }

    public void updateTargetDate(Instant targetDate) {
        this.targetDate = targetDate;
    }

    public void updateStatus(ReadingGoalStatus status) {
        this.status = status;
    }

    public int calculateRemainingDay(Instant now) {
        LocalDate today = now.atZone(KST).toLocalDate();
        LocalDate target = targetDate.atZone(KST).toLocalDate();
        int remainingDay = (int) ChronoUnit.DAYS.between(today, target);
        return remainingDay;
    }

    public int calculateTodayTargetPage(int remainingDay, Instant now) {
        int todayRead = getTodayReadPages(now);
        int pageAtStartOfToday = this.currentPage - todayRead;
        int remainingAtStart = this.targetPage - pageAtStartOfToday;

        if (remainingDay <= 0) {
            return remainingAtStart;
        }

        return (remainingAtStart + remainingDay) / (remainingDay + 1);
    }

    public int calculatePercent() {
        if (targetPage == 0) {
            return 0;
        }
        return (int) (this.currentPage * 100L / this.targetPage);
    }

    public int getTodayReadPages(Instant now) {
        return isSameDay(this.currentPageUpdatedAt, now) ? this.todayReadPages : 0;
    }

    private boolean isSameDay(Instant day1, Instant day2) {
        if (day1 == null || day2 == null) {
            return false;
        }
        LocalDate dayA = day1.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate dayB = day2.atZone(ZoneOffset.UTC).toLocalDate();
        return dayA.equals(dayB);
    }

    public static ReadingGoal from(Long userId, Long bookId, Instant targetDate, int targetPage) {
        return new ReadingGoal(
                userId,
                bookId,
                targetDate,
                targetPage,
                0,
                ReadingGoalStatus.IN_PROGRESS
        );
    }
}
