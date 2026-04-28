package dev.euns.studyfit.infrastructure.comsi.client;

import dev.euns.studyfit.global.exception.BaseException;
import dev.euns.studyfit.infrastructure.comsi.exception.ComsiErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class ComciClient {

    private static final Charset EUC_KR = Charset.forName("EUC-KR");
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();
    private static final String SEARCH_PREFIX = "17384l";
    private static final String TIMETABLE_PREFIX = "73629";

    private final RestClient restClient;
    private final String baseUrl;

    public ComciClient(@Value("${comsi.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder().build();
    }

    public String searchSchools(String keyword) {
        String trimmedKeyword = keyword == null ? "" : keyword.trim();
        if (trimmedKeyword.isEmpty()) {
            throw new BaseException(ComsiErrorCode.INVALID_KEYWORD);
        }

        return fetch(SEARCH_PREFIX + encodeAsEucKr(trimmedKeyword));
    }

    public String fetchTimetable(int schoolCode, int cacheVersion, int dateIndex) {
        return fetch(encodeTimetableQuery(schoolCode, cacheVersion, dateIndex));
    }

    private String fetch(String rawQuery) {
        URI uri = URI.create(baseUrl + "?" + rawQuery);

        try {
            byte[] body = restClient.get()
                    .uri(uri)
                    .header("Accept", "*/*")
                    .retrieve()
                    .body(byte[].class);

            if (body == null) {
                throw new BaseException(ComsiErrorCode.MALFORMED_RESPONSE);
            }

            return ResponseBodyDecoder.decode(body);
        } catch (RestClientResponseException | ResourceAccessException e) {
            log.warn("컴시간 API 호출 실패: {}", e.getMessage());
            throw new BaseException(ComsiErrorCode.HTTP_ERROR);
        }
    }

    private static String encodeTimetableQuery(int schoolCode, int cacheVersion, int dateIndex) {
        String payload = TIMETABLE_PREFIX + "_" + schoolCode + "_" + cacheVersion + "_" + dateIndex;
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    private static String encodeAsEucKr(String text) {
        byte[] bytes = text.getBytes(EUC_KR)
        StringBuilder encoded = new StringBuilder(bytes.length * 3);

        for (byte value : bytes) {
            int unsigned = value & 0xFF;
            encoded.append('%')
                    .append(HEX[unsigned >>> 4])
                    .append(HEX[unsigned & 0x0F]);
        }

        return encoded.toString();
    }
}
