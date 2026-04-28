package dev.euns.studyfit.infrastructure.comsi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ClassSelectionResponse {
    private final School school;
    private final List<ClassOption> classes;
    private final String summary;
}
