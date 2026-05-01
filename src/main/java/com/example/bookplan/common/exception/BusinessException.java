package com.example.bookplan.common.exception;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {
    public abstract HttpStatus getStatus();
    public abstract String getCode();
    protected BusinessException(String message) {
        super(message);
    }
}
