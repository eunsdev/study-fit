package dev.euns.studyfit.infrastructure.comsi.parser;

import dev.euns.studyfit.infrastructure.comsi.dto.response.School;
import dev.euns.studyfit.infrastructure.comsi.dto.response.SchoolSearchResponse;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public final class SchoolSearchParser {

    private static final String SEARCH_RESULT_FIELD = "학교검색";
    private static final int REGION_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int CODE_INDEX = 3;
    private static final int REQUIRED_COLUMNS = 4;

    private SchoolSearchParser() {}

    public static SchoolSearchResponse parse(JsonNode root) {
        List<School> schools = new ArrayList<>();
        JsonNode rows = root.path(SEARCH_RESULT_FIELD);

        if (rows.isArray()) {
            for (JsonNode row : rows) {
                School school = readSchool(row);
                if (school != null) {
                    schools.add(school);
                }
            }
        }

        return new SchoolSearchResponse(schools, searchSummary(schools.size()));
    }

    private static School readSchool(JsonNode row) {
        if (!row.isArray() || row.size() < REQUIRED_COLUMNS) {
            return null;
        }

        int code = row.get(CODE_INDEX).asInt(0);
        if (code == 0) {
            return null;
        }

        return new School(
                code,
                row.get(NAME_INDEX).asText(""),
                row.get(REGION_INDEX).asText("")
        );
    }

    private static String searchSummary(int count) {
        if (count == 0) {
            return "검색 결과가 없습니다.";
        }

        return count + "개의 학교를 찾았습니다.";
    }
}
