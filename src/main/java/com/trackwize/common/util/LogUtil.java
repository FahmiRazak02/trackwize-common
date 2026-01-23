package com.trackwize.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for logging and formatting data in a readable way.
 * <p>
 * Provides methods to:
 * <ul>
 *     <li>Pretty-print JSON objects or strings</li>
 *     <li>Format HTTP response headers</li>
 *     <li>Format log bodies, truncating long content</li>
 *     <li>Log content with a consistent indentation style</li>
 * </ul>
 * <p>
 * This class is non-instantiable and all methods are static.
 */
@Slf4j
public class LogUtil {

    private static final int MAX_BODY_LOG_LENGTH = 500;
    private static final int DEFAULT_LINE_LENGTH = 100;

    private LogUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Formats HTTP response headers into a human-readable string.
     *
     * @param response HttpServletResponse containing headers
     * @return formatted string of headers with indentation
     */
    public static String formatHeaders(HttpServletResponse response) {
        String sep = System.lineSeparator();
        return response.getHeaderNames().stream()
                .map(header -> "        " + header + " : " + response.getHeader(header)) // indent
                .collect(Collectors.joining(sep, "{" + sep, sep + "    }")); // use System.lineSeparator
    }

    /**
     * Converts an object into a pretty-printed JSON string.
     *
     * @param obj object to convert
     * @return pretty-printed JSON, or obj.toString() if conversion fails
     */
    public static String prettyPrintJson(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    /**
     * Converts a JSON string into a pretty-printed JSON string.
     *
     * @param json raw JSON string
     * @return pretty-printed JSON, or original string if invalid JSON
     */
    public static String prettyPrintJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object parsed = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
        } catch (Exception e) {
            return json; // not JSON → log as-is
        }
    }

    /**
     * Formats a body object for logging.
     * <p>
     * If the body is JSON-like (Map, Iterable, or starts with '{' or '['), it will be pretty-printed.
     * Long content is truncated to {@link #MAX_BODY_LOG_LENGTH}.
     *
     * @param body the object to format
     * @return formatted string suitable for logging
     */
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

    /**
     * Logs a string with a label and indentation for multi-line content.
     *
     * @param label   label for the log
     * @param content content to log
     */
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

    /**
     * Generates a line of repeated characters.
     *
     * @param c      character to repeat
     * @param length length of the line, uses {@link #DEFAULT_LINE_LENGTH} if null
     * @return string with repeated characters
     */
    public static String repeatCharLine(char c, Integer length) {
        if (length == null)
            length = DEFAULT_LINE_LENGTH;

        return String.valueOf(c).repeat(Math.max(0, length));
    }

    /**
     * Logs an object or string as pretty JSON with indentation.
     *
     * @param log   SLF4J logger to use
     * @param label label for the log
     * @param body  object or string to pretty-print and log
     */
    public static void logPrettyJson(org.slf4j.Logger log, String label, Object body) {
        if (body == null) {
            log.info("    {} : <empty>", label);
            return;
        }

        String content;
        try {
            if (body instanceof String) {
                content = prettyPrintJson((String) body);
            } else {
                content = prettyPrintJson(body);
            }
        } catch (Exception e) {
            content = String.valueOf(body);
        }

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
}
