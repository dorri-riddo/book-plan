package com.example.bookplan.notification.exception;

import com.example.bookplan.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotFoundDeviceException extends BusinessException {
    public NotFoundDeviceException(String deviceId) {
        super("등록되지 않은 기기입니다: " + deviceId);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getCode() {
        return "DEVICE_NOT_FOUND";
    }
}
