package com.example.bookplan.user.exception;

import com.example.bookplan.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String email) {
        super("이미 등록된 이메일입니다: " + email);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getCode() {
        return "DUPLICATE_EMAIL";
    }
}
