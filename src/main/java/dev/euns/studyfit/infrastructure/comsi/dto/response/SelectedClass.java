package dev.euns.studyfit.infrastructure.comsi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SelectedClass {
    private final int grade;
    private final int classNumber;
    private final String label;

    public static SelectedClass of(int grade, int classNumber) {
        return new SelectedClass(grade, classNumber, grade + "-" + classNumber + "반");
    }
}
