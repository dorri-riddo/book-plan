package com.example.bookplan.auth.exception;

import com.example.bookplan.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException() {
        super("유효하지 않은 리프레시 토큰입니다");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getCode() {
        return "INVALID_REFRESH_TOKEN";
    }
}
