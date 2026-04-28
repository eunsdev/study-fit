package dev.euns.studyfit.infrastructure.comsi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClassOption {
    private final int grade;
    private final int classNumber;
    private final String id;
    private final String label;

    public static ClassOption of(int grade, int classNumber) {
        String id = grade + "-" + classNumber;
        return new ClassOption(grade, classNumber, id, id + "반");
    }
}
