package dev.euns.studyfit.infrastructure.comsi.parser;

import tools.jackson.databind.JsonNode;

final class ComsiJsonUtils {

    private ComsiJsonUtils() {}

    static JsonNode arrayAt(JsonNode array, int index) {
        if (array == null || !array.isArray() || index < 0) {
            return null;
        }

        if (index < array.size()) {
            return array.get(index);
        }

        // 컴시간 응답은 배열마다 0-based, 1-based가 섞여 들어온다.
        int shiftedIndex = index - 1;
        if (shiftedIndex >= 0 && shiftedIndex < array.size()) {
            return array.get(shiftedIndex);
        }

        return null;
    }

    static int intAt(JsonNode data, int... path) {
        JsonNode node = walk(data, path);
        return (node != null && node.isNumber()) ? node.asInt() : 0;
    }

    static String textAt(JsonNode data, int... path) {
        JsonNode node = walk(data, path);
        return (node != null && node.isTextual()) ? node.asText() : null;
    }

    static String nameAt(JsonNode source, int id) {
        JsonNode node = arrayAt(source, id);
        if (node == null) {
            return "";
        }

        if (node.isTextual()) {
            return node.asText();
        }

        if (node.isArray() && !node.isEmpty()) {
            JsonNode last = node.get(node.size() - 1);
            if (last.isTextual()) {
                return last.asText();
            }
        }

        return "";
    }

    private static JsonNode walk(JsonNode node, int... path) {
        for (int index : path) {
            node = arrayAt(node, index);
            if (node == null) {
                return null;
            }
        }

        return node;
    }
}
