package com.example.bookplan.auth.exception;

import com.example.bookplan.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotFoundEmailException extends BusinessException {
    public NotFoundEmailException(String email) {
        super("존재하지 않는 이메일입니다: " + email);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getCode() {
        return "NOT_FOUND_EMAIL";
    }
}
