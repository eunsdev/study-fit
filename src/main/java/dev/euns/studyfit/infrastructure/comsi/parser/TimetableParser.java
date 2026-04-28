package dev.euns.studyfit.infrastructure.comsi.parser;

import dev.euns.studyfit.infrastructure.comsi.dto.response.ClassOption;
import dev.euns.studyfit.infrastructure.comsi.dto.response.ClassSelectionResponse;
import dev.euns.studyfit.infrastructure.comsi.dto.response.DateOption;
import dev.euns.studyfit.infrastructure.comsi.dto.response.PeriodSelectionResponse;
import dev.euns.studyfit.infrastructure.comsi.dto.response.School;
import dev.euns.studyfit.infrastructure.comsi.dto.response.SelectedClass;
import dev.euns.studyfit.infrastructure.comsi.dto.response.TimetableCell;
import dev.euns.studyfit.infrastructure.comsi.dto.response.TimetableResponse;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public final class TimetableParser {

    private static final String FIELD_SCHOOL_NAME = "학교명";
    private static final String FIELD_REGION = "지역명";
    private static final String FIELD_CLASS_COUNTS = "학급수";
    private static final String FIELD_VIRTUAL_CLASS_COUNTS = "가상학급수";
    private static final String FIELD_DATES = "일자자료";
    private static final String FIELD_TODAY_INDEX = "오늘r";
    private static final String FIELD_SEPARATOR = "분리";
    private static final String FIELD_CLASSROOM_ENABLED = "강의실";
    private static final String FIELD_CURRENT_TIMETABLE = "자료147";
    private static final String FIELD_BASE_TIMETABLE = "자료481";
    private static final String FIELD_TEACHERS = "자료446";
    private static final String FIELD_SUBJECTS = "자료492";
    private static final String FIELD_CLASSROOMS = "자료245";

    private static final int WEEKDAYS = 5;
    private static final int PERIODS = 8;
    private static final int DEFAULT_SEPARATOR = 100;

    private TimetableParser() {}

    public static ClassSelectionResponse toClassSelection(JsonNode root, int schoolCode) {
        School school = readSchool(root, schoolCode);
        List<ClassOption> classes = readClassOptions(root);
        DateOption firstDate = firstDateOf(readDateOptions(root));

        return new ClassSelectionResponse(school, classes, buildSummary(school, root, null, firstDate));
    }

    public static PeriodSelectionResponse toPeriodSelection(JsonNode root, int schoolCode) {
        School school = readSchool(root, schoolCode);
        List<DateOption> dates = readDateOptions(root);
        Integer todayIndex = readTodayIndex(root);
        DateOption firstDate = firstDateOf(dates);

        return new PeriodSelectionResponse(school, dates, todayIndex, buildSummary(school, root, null, firstDate));
    }

    public static TimetableResponse toTimetable(
            JsonNode root, int schoolCode, int grade, int classNumber, int dateIndex
    ) {
        School school = readSchool(root, schoolCode);
        SelectedClass selected = SelectedClass.of(grade, classNumber);
        DateOption selectedDate = findDate(readDateOptions(root), dateIndex);
        List<List<TimetableCell>> grid = readTimetable(root, grade, classNumber);

        return new TimetableResponse(school, selected, selectedDate, selected.getLabel() + " 시간표", grid);
    }

    private static School readSchool(JsonNode root, int schoolCode) {
        return new School(
                schoolCode,
                root.path(FIELD_SCHOOL_NAME).asText(""),
                root.path(FIELD_REGION).asText("")
        );
    }

    private static List<ClassOption> readClassOptions(JsonNode root) {
        JsonNode counts = root.path(FIELD_CLASS_COUNTS);
        List<ClassOption> options = new ArrayList<>();

        if (!counts.isArray() || counts.size() <= 1) {
            return options;
        }

        for (int grade = 1; grade < counts.size(); grade++) {
            int classCount = visibleClassCount(root, counts, grade);
            for (int classNumber = 1; classNumber <= classCount; classNumber++) {
                options.add(ClassOption.of(grade, classNumber));
            }
        }

        return options;
    }

    private static int visibleClassCount(JsonNode root, JsonNode counts, int grade) {
        int total = Math.max(counts.get(grade).asInt(0), 0);
        int virtual = virtualClassCount(root.path(FIELD_VIRTUAL_CLASS_COUNTS), grade);

        return Math.max(total - virtual, 0);
    }

    private static int virtualClassCount(JsonNode virtualCounts, int grade) {
        if (!virtualCounts.isArray() || grade >= virtualCounts.size()) {
            return 0;
        }

        return Math.max(virtualCounts.get(grade).asInt(0), 0);
    }

    private static List<DateOption> readDateOptions(JsonNode root) {
        JsonNode rawDates = root.path(FIELD_DATES);
        List<DateOption> dates = new ArrayList<>();

        if (!rawDates.isArray()) {
            return dates;
        }

        for (int i = 0; i < rawDates.size(); i++) {
            DateOption date = readDate(rawDates.get(i), i + 1);
            if (date != null) {
                dates.add(date);
            }
        }

        return dates;
    }

    private static DateOption readDate(JsonNode node, int fallbackIndex) {
        if (node.isArray() && !node.isEmpty()) {
            int index = node.get(0).asInt(fallbackIndex);
            String label = node.size() > 1
                    ? node.get(1).asText(String.valueOf(index))
                    : String.valueOf(index);

            return new DateOption(index, label);
        }

        if (node.isTextual()) {
            return new DateOption(fallbackIndex, node.asText());
        }

        return null;
    }

    private static Integer readTodayIndex(JsonNode root) {
        JsonNode node = root.path(FIELD_TODAY_INDEX);
        if (!node.isNumber()) {
            return null;
        }

        return node.asInt();
    }

    private static DateOption findDate(List<DateOption> dates, int dateIndex) {
        for (DateOption date : dates) {
            if (date.getIndex() == dateIndex) {
                return date;
            }
        }

        return dates.isEmpty() ? new DateOption(dateIndex, "") : dates.get(0);
    }

    private static DateOption firstDateOf(List<DateOption> dates) {
        return dates.isEmpty() ? null : dates.get(0);
    }

    private static List<List<TimetableCell>> readTimetable(JsonNode root, int grade, int classNumber) {
        int separator = readSeparator(root);
        boolean classroomEnabled = root.path(FIELD_CLASSROOM_ENABLED).asInt(0) == 1;
        JsonNode current = root.path(FIELD_CURRENT_TIMETABLE);
        JsonNode base = root.path(FIELD_BASE_TIMETABLE);
        JsonNode teachers = root.path(FIELD_TEACHERS);
        JsonNode subjects = root.path(FIELD_SUBJECTS);
        JsonNode classrooms = root.path(FIELD_CLASSROOMS);

        List<List<TimetableCell>> grid = new ArrayList<>(WEEKDAYS);
        for (int weekday = 1; weekday <= WEEKDAYS; weekday++) {
            List<TimetableCell> row = new ArrayList<>(PERIODS);
            for (int period = 1; period <= PERIODS; period++) {
                row.add(CellDecoder.decode(
                        current, base, subjects, teachers, classrooms,
                        separator, classroomEnabled,
                        grade, classNumber, weekday, period
                ));
            }
            grid.add(row);
        }

        return grid;
    }

    private static int readSeparator(JsonNode root) {
        int raw = root.path(FIELD_SEPARATOR).asInt(DEFAULT_SEPARATOR);
        return raw > 0 ? raw : DEFAULT_SEPARATOR;
    }

    private static String buildSummary(School school, JsonNode root, SelectedClass selected, DateOption date) {
        List<String> lines = new ArrayList<>(5);
        lines.add(formatSchool(school));
        lines.add(formatClassCounts(root));
        lines.add("오늘 인덱스: " + formatTodayIndex(root));
        lines.add("일자: " + (date != null ? date.getLabel() : "일자 정보 없음"));

        if (selected != null) {
            lines.add("선택 학급: " + selected.getLabel());
        }

        return String.join("\n", lines);
    }

    private static String formatSchool(School school) {
        if (school.getRegion() == null || school.getRegion().isBlank()) {
            return school.getName();
        }

        return school.getName() + " (" + school.getRegion() + ")";
    }

    private static String formatClassCounts(JsonNode root) {
        JsonNode counts = root.path(FIELD_CLASS_COUNTS);
        if (!counts.isArray() || counts.isEmpty()) {
            return "학급수 정보 없음";
        }

        List<String> parts = new ArrayList<>(counts.size());
        for (JsonNode count : counts) {
            parts.add(String.valueOf(count.asInt()));
        }

        return "학급수: " + String.join(", ", parts);
    }

    private static String formatTodayIndex(JsonNode root) {
        JsonNode node = root.path(FIELD_TODAY_INDEX);
        return node.isNumber() ? String.valueOf(node.asInt()) : "없음";
    }
}
