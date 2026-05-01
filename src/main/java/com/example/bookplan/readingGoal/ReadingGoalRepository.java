package com.example.bookplan.readingGoal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingGoalRepository extends JpaRepository<ReadingGoal, Long> {
    Optional<ReadingGoal> findByIdAndUserId(Long readingGoalId, Long userId);
    List<ReadingGoal> findAllByUserId(Long userId);
    List<ReadingGoal> findAllByUserIdAndStatus(Long userId, ReadingGoalStatus status);
    long deleteByIdAndUserId(Long readingGoalId, Long userId);
}
