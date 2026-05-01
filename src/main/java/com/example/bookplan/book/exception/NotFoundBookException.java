package com.example.bookplan.book.exception;

import com.example.bookplan.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotFoundBookException extends BusinessException {
    public NotFoundBookException(Long bookId) {
        super("존재하지 않는 책입니다: " + bookId);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getCode() {
        return "BOOK_NOT_FOUND";
    }
}
