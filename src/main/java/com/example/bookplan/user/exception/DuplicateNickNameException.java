package com.example.bookplan.user.exception;

import com.example.bookplan.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateNickNameException extends BusinessException {
    public DuplicateNickNameException(String nickName) {
        super("이미 등록된 닉네임입니다: " + nickName);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getCode() {
        return "DUPLICATE_NICK_NAME";
    }
}
