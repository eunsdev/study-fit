package dev.euns.studyfit.infrastructure.comsi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class School {
    private final int code;
    private final String name;
    private final String region;
}
