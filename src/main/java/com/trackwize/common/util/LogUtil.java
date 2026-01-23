package com.trackwize.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class LogUtil {

    private static final int MAX_BODY_LOG_LENGTH = 500;
    private static final int DEFAULT_LINE_LENGTH = 100;

    private LogUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String formatHeaders(HttpServletResponse response) {
        String sep = System.lineSeparator();
        return response.getHeaderNames().stream()
                .map(header -> "        " + header + " : " + response.getHeader(header)) // indent
                .collect(Collectors.joining(sep, "{" + sep, sep + "    }")); // use System.lineSeparator
    }

    public static String prettyPrintJson(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    public static String prettyPrintJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object parsed = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
        } catch (Exception e) {
            return json; // not JSON → log as-is
        }
    }

    public static String formatBody(Object body) {
        if (body == null) return "<empty>";

        String content;
        if (body instanceof Map || body instanceof Iterable || body.toString().startsWith("{") || body.toString().startsWith("[")) {
            content = prettyPrintJson(body);
        } else {
            content = String.valueOf(body);
        }

        if (content.length() > MAX_BODY_LOG_LENGTH) {
            content = content.substring(0, MAX_BODY_LOG_LENGTH) + "... (truncated)";
        }

        return content;
    }

    public static void logWithPrefix(String label, String content) {
        String sep = System.lineSeparator();
        boolean first = true;
        for (String line : content.split(sep)) {
            if (first) {
                log.info("    {} : {}", label, line);
                first = false;
            } else {
                log.info("        {}", line);
            }
        }
    }

    public static void logPrettyJson(org.slf4j.Logger log, String label, String content) {
        String sep = System.lineSeparator();
        for (String line : content.split(sep)) {
            log.info("{} {}", label, line);
        }
    }

    public static String repeatCharLine(char c, Integer length) {
        if (length == null)
            length = DEFAULT_LINE_LENGTH;

        return String.valueOf(c).repeat(Math.max(0, length));
    }
}
