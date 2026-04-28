package dev.euns.studyfit.infrastructure.comsi.parser;

import dev.euns.studyfit.infrastructure.comsi.dto.response.TimetableCell;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

final class CellDecoder {

    private static final int EMPTY_THRESHOLD = 100;
    private static final int LEGACY_SEPARATOR = 100;
    private static final int TEACHER_NAME_MAX = 2;

    private CellDecoder() {}

    static TimetableCell decode(
            JsonNode currentData, JsonNode baseData,
            JsonNode subjectData, JsonNode teacherData, JsonNode classroomData,
            int separator, boolean classroomEnabled,
            int grade, int classNumber, int weekday, int period
    ) {
        int currentCode = ComsiJsonUtils.intAt(currentData, grade, classNumber, weekday, period);
        int baseCode = ComsiJsonUtils.intAt(baseData, grade, classNumber, weekday, period);
        boolean changed = currentCode != baseCode;

        if (currentCode <= EMPTY_THRESHOLD) {
            return TimetableCell.empty(changed);
        }

        LessonCode lesson = LessonCode.parse(currentCode, separator);
        String subject = ComsiJsonUtils.nameAt(subjectData, lesson.subjectId());
        String teacher = shortenTeacherName(ComsiJsonUtils.nameAt(teacherData, lesson.teacherId()));
        String classroom = null;

        if (classroomEnabled) {
            classroom = parseClassroom(ComsiJsonUtils.textAt(classroomData, grade, classNumber, weekday, period));
        }

        return new TimetableCell(formatText(lesson.prefix(), subject, teacher, classroom), changed);
    }

    private record LessonCode(int teacherId, int subjectId, String prefix) {

        private static LessonCode parse(int value, int separator) {
            if (separator == LEGACY_SEPARATOR) {
                return new LessonCode(value / 100, value % 100, "");
            }

            return new LessonCode(
                    value % separator,
                    (value / separator) % separator,
                    prefixOf(value, separator)
            );
        }

        private static String prefixOf(int value, int separator) {
            int index = value / separator / separator;
            if (index < 1 || index > 26) {
                return "";
            }

            return ((char) ('A' + index - 1)) + "_";
        }
    }

    private static String shortenTeacherName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }

        return fullName.length() <= TEACHER_NAME_MAX
                ? fullName
                : fullName.substring(0, TEACHER_NAME_MAX);
    }

    private static String parseClassroom(String raw) {
        if (raw == null) {
            return null;
        }

        String[] parts = raw.split("_", 2);
        if (parts.length != 2 || parseFloor(parts[0]) <= 0) {
            return null;
        }

        String room = parts[1].trim();
        return room.isEmpty() ? null : room;
    }

    private static int parseFloor(String rawFloor) {
        try {
            return Integer.parseInt(rawFloor.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String formatText(String prefix, String subject, String teacher, String classroom) {
        StringBuilder text = new StringBuilder(prefix).append(subject);

        List<String> details = new ArrayList<>(2);
        if (!teacher.isEmpty()) {
            details.add(teacher);
        }
        if (classroom != null && !classroom.isEmpty()) {
            details.add(classroom);
        }

        if (!details.isEmpty()) {
            text.append(' ').append(String.join(" ", details));
        }

        return text.toString();
    }
}
