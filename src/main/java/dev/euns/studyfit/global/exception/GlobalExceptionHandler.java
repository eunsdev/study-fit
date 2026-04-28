package dev.euns.studyfit.global.exception;

import dev.euns.studyfit.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public BaseResponse<Void> handle(BaseException e, HttpServletResponse response) {

        ErrorCode errorCode = e.getErrorCode();

        response.setStatus(errorCode.getStatus());

        return BaseResponse.error(
                errorCode.getStatus(),
                errorCode.getMessage()
        );
    }
}