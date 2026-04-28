package dev.euns.studyfit.infrastructure.comsi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PeriodSelectionResponse {
    private final School school;
    private final List<DateOption> dates;
    private final Integer todayDateIndex;
    private final String summary;
}
