package com.example.bookplan.readingGoal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingGoalRepository extends JpaRepository<ReadingGoal, Long> {
    Optional<ReadingGoal> findByIdAndUserId(Long readingGoalId, Long userId);
    List<ReadingGoal> findAllByUserIdOrderByIdDesc(Long userId);
    List<ReadingGoal> findAllByUserIdAndStatusOrderByIdDesc(Long userId, ReadingGoalStatus status);
    List<ReadingGoal> findAllByUserIdAndBookIdOrderByIdDesc(Long userId, Long bookId);
    long deleteByIdAndUserId(Long readingGoalId, Long userId);
}
