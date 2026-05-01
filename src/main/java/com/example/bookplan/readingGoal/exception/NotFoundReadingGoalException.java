package com.example.bookplan.readingGoal.exception;

import com.example.bookplan.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotFoundReadingGoalException extends BusinessException {
    public NotFoundReadingGoalException(Long readingGoalId) {
        super("존재하지 않는 목표입니다: " + readingGoalId);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getCode() {
        return "READING_GOAL_NOT_FOUND";
    }
}
