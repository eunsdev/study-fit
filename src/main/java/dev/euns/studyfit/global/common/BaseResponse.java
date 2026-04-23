package dev.euns.studyfit.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class BaseResponse<T> {
    private final int code;
    private final String message;
    private final T data;

    private BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, null, data);
    }

    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>(200, message, null);
    }

    public static BaseResponse<Void> error(int code, String message) {
        return new BaseResponse<>(code, message, null);
    }
}