package com.example.bookplan.auth.exception;

import com.example.bookplan.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class WrongPasswordException extends BusinessException {
    public WrongPasswordException() {
        super("잘못된 비밀번호입니다");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getCode() {
        return "WRONG_PASSWORD";
    }
}
