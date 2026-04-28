package dev.euns.studyfit.infrastructure.comsi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TimetableResponse {
    private final School school;
    private final SelectedClass selectedClass;
    private final DateOption selectedDate;
    private final String headerTitle;
    private final List<List<TimetableCell>> grid;
}
