package dev.euns.studyfit.infrastructure.comsi.service;

import dev.euns.studyfit.global.exception.BaseException;
import dev.euns.studyfit.infrastructure.comsi.client.ComciClient;
import dev.euns.studyfit.infrastructure.comsi.dto.response.ClassSelectionResponse;
import dev.euns.studyfit.infrastructure.comsi.dto.response.PeriodSelectionResponse;
import dev.euns.studyfit.infrastructure.comsi.dto.response.SchoolSearchResponse;
import dev.euns.studyfit.infrastructure.comsi.dto.response.TimetableResponse;
import dev.euns.studyfit.infrastructure.comsi.exception.ComsiErrorCode;
import dev.euns.studyfit.infrastructure.comsi.parser.SchoolSearchParser;
import dev.euns.studyfit.infrastructure.comsi.parser.TimetableParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ComsiService {

    private static final int DEFAULT_CACHE_VERSION = 0;
    private static final int DEFAULT_DATE_INDEX = 1;
    private static final String EMPTY_KEYWORD_MESSAGE = "학교명을 입력해주세요.";

    private final ComciClient comciClient;
    private final ObjectMapper objectMapper;

    public SchoolSearchResponse searchSchools(String keyword) {
        String trimmedKeyword = keyword == null ? "" : keyword.trim();
        if (trimmedKeyword.isEmpty()) {
            return SchoolSearchResponse.empty(EMPTY_KEYWORD_MESSAGE);
        }

        JsonNode root = readJson(comciClient.searchSchools(trimmedKeyword));
        return SchoolSearchParser.parse(root);
    }

    public ClassSelectionResponse getClassSelection(int schoolCode) {
        JsonNode root = fetchTimetableJson(schoolCode, DEFAULT_DATE_INDEX);
        return TimetableParser.toClassSelection(root, schoolCode);
    }

    public PeriodSelectionResponse getPeriodSelection(int schoolCode) {
        JsonNode root = fetchTimetableJson(schoolCode, DEFAULT_DATE_INDEX);
        return TimetableParser.toPeriodSelection(root, schoolCode);
    }

    public TimetableResponse getTimetable(int schoolCode, int grade, int classNumber, int dateIndex) {
        JsonNode root = fetchTimetableJson(schoolCode, dateIndex);
        return TimetableParser.toTimetable(root, schoolCode, grade, classNumber, dateIndex);
    }

    private JsonNode fetchTimetableJson(int schoolCode, int dateIndex) {
        String json = comciClient.fetchTimetable(schoolCode, DEFAULT_CACHE_VERSION, dateIndex);
        return readJson(json);
    }

    private JsonNode readJson(String rawJson) {
        try {
            return objectMapper.readTree(rawJson);
        } catch (JacksonException e) {
            throw new BaseException(ComsiErrorCode.MALFORMED_RESPONSE);
        }
    }
}
