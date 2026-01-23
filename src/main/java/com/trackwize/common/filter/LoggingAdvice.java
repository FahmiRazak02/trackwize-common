package com.trackwize.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackwize.common.util.LogUtil;
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
            String requestTimeStr = (String) httpRequest.getServletRequest().getAttribute("X-Request-Time");
            Instant requestTime = requestTimeStr != null ? Instant.parse(requestTimeStr) : Instant.now();
            Instant responseTime = Instant.now();
            long durationMs = Duration.between(requestTime, responseTime).toMillis();

            servletResponse.setHeader("X-Response-Time", responseTime.toString());
            servletResponse.setHeader("X-Processing-Time", durationMs + "ms");

            String userId = servletResponse.getHeader("X-User-ID");

            log.info(LogUtil.repeatCharLine('=', null));
            log.info("Outgoing Response");
            log.info("    status      : {}", servletResponse.getStatus());
            log.info("    userId      : {}", userId != null ? userId : "anonymous");
            log.info("    time        : {}ms", durationMs);
            LogUtil.logWithPrefix("headers", LogUtil.formatHeaders(servletResponse));
            LogUtil.logWithPrefix("body", LogUtil.formatBody(body));
            log.info(LogUtil.repeatCharLine('=', null));

        } catch (Exception e) {
            log.warn("Failed to log response: {}", e.getMessage());
        }

        return body;
    }

}
