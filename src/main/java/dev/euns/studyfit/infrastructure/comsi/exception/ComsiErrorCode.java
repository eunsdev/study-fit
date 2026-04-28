package dev.euns.studyfit.infrastructure.comsi.exception;

import dev.euns.studyfit.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ComsiErrorCode implements ErrorCode {
    INVALID_KEYWORD(400, "검색 키워드가 비어있습니다"),
    HTTP_ERROR(502, "컴시간 API 호출 실패"),
    DECODE_ERROR(502, "응답 디코딩 실패"),
    MALFORMED_RESPONSE(502, "응답 형식이 올바르지 않습니다");

    private final int status;
    private final String message;
}