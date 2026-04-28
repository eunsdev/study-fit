package dev.euns.studyfit.infrastructure.comsi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimetableCell {
    private final String text;
    private final boolean changed;

    public static TimetableCell empty(boolean changed) {
        return new TimetableCell("", changed);
    }
}
