package com.example.bookplan.readingGoal.exception;

import com.example.bookplan.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidCurrentPageException extends BusinessException {
    public InvalidCurrentPageException(Long readingGoalId) {
        super("책 페이지보다 읽은 페이지가 더 많습니다: " + readingGoalId);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getCode() {
        return "READING_GOAL_INVALID_CURRENT_PAGE";
    }
}
