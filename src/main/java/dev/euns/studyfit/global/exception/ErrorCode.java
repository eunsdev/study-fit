package dev.euns.studyfit.global.exception;

public interface ErrorCode {
    int getStatus();
    int getCode();
    String getMessage();
}