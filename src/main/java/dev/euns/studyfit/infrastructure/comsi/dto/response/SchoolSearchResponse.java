package dev.euns.studyfit.infrastructure.comsi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SchoolSearchResponse {
    private final List<School> schools;
    private final String summary;

    public static SchoolSearchResponse empty(String summary) {
        return new SchoolSearchResponse(List.of(), summary);
    }
}
