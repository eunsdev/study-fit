package dev.euns.studyfit.global.common.exception;

public interface ErrorCode {
    int getStatus();
    int getCode();
    String getMessage();
}