package com.trackwize.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class LoggingAdvice implements ResponseBodyAdvice<Object> {

    private static final int MAX_BODY_LOG_LENGTH = 500;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        if (!(response instanceof ServletServerHttpResponse httpResponse)
                || !(request instanceof ServletServerHttpRequest httpRequest)) {
            return body;
        }

        HttpServletResponse servletResponse = httpResponse.getServletResponse();

        try {
            // --- Calculate processing time ---
            String requestTimeStr = (String) httpRequest.getServletRequest().getAttribute("X-Request-Time");
            Instant requestTime = requestTimeStr != null ? Instant.parse(requestTimeStr) : Instant.now();
            Instant responseTime = Instant.now();
            long durationMs = Duration.between(requestTime, responseTime).toMillis();

            servletResponse.setHeader("X-Response-Time", responseTime.toString());
            servletResponse.setHeader("X-Processing-Time", durationMs + "ms");

            String userId = servletResponse.getHeader("X-User-ID");

            int lineLength = 100;
            log.info(repeatCharLine('=', lineLength));
            log.info("Outgoing Response");
            log.info("    status      : {}", servletResponse.getStatus());
            log.info("    userId      : {}", userId != null ? userId : "anonymous");
            log.info("    time        : {}ms", durationMs);
            logWithPrefix("headers", formatHeaders(servletResponse));
            logWithPrefix("body", formatBody(body));
            log.info(repeatCharLine('-', lineLength));

        } catch (Exception e) {
            log.warn("Failed to log response: {}", e.getMessage());
        }

        return body;
    }

    private String formatHeaders(HttpServletResponse response) {
        String sep = System.lineSeparator();
        return response.getHeaderNames().stream()
                .map(header -> "        " + header + " : " + response.getHeader(header)) // indent
                .collect(Collectors.joining(sep, "{" + sep, sep + "    }")); // use System.lineSeparator
    }

    private String prettyPrintJson(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    private String formatBody(Object body) {
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

    private void logWithPrefix(String label, String content) {
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

    public static String repeatCharLine(char c, int length) {
        return String.valueOf(c).repeat(Math.max(0, length));
    }

}
