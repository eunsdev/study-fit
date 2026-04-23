package dev.euns.studyfit.global.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    protected BaseException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}