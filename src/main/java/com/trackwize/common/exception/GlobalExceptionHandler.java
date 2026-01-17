package com.trackwize.common.exception;

import com.trackwize.common.constant.ErrorConst;
import com.trackwize.common.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseUtil handleMissingRequestParam(MissingServletRequestParameterException e) {
        log.error("Missing request parameter: {}", e.getMessage(), e);
        String message = String.format("Missing required parameter: '%s'", e.getParameterName());
        return ResponseUtil.createErrorResponse(
                ErrorConst.NOT_EXIST_CODE,
                message
        );
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseUtil handleMissingPathVariable(MissingPathVariableException e) {
        log.error("Missing path variable: {}", e.getMessage(), e);
        String message = String.format("Missing required path variable: '%s'", e.getVariableName());
        return ResponseUtil.createErrorResponse(
                ErrorConst.NOT_EXIST_CODE,
                message
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseUtil handleNoResourceFound(NoResourceFoundException e) {
        log.error("No resource found: {}", e.getMessage(), e);
        return ResponseUtil.createErrorResponse(
                ErrorConst.NOT_EXIST_CODE,
                "Invalid API endpoint or missing path variable."
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseUtil handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        // First error only (client)
        FieldError firstError = fieldErrors.get(0);
        String message = firstError.getField() + ": " + firstError.getDefaultMessage();

        // Log ALL errors (ops / debugging)
        Map<String, String> allErrors = new LinkedHashMap<>();
        fieldErrors.forEach(error ->
                allErrors.put(error.getField(), error.getDefaultMessage())
        );

        log.error("Validation failed. All errors: {}", allErrors);

        return ResponseUtil.createErrorResponse(
                ErrorConst.INVALID_INPUT_CODE,
                message
        );
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseUtil handleMissingCookie(MissingRequestCookieException e) {
        log.warn("Missing request cookie: {}", e.getMessage());
        return ResponseUtil.createErrorResponse(
                ErrorConst.MISSING_REFRESH_TOKEN_CODE,
                ErrorConst.MISSING_REFRESH_TOKEN_MSG
        );
    }

    @ExceptionHandler(TrackWizeException.class)
    public ResponseUtil handleKWAPException(TrackWizeException e) {
        log.error("TrackWizeException occurred: {}", e.getMessage(), e);
        return ResponseUtil.createErrorResponse(
                e.getMessageCode(),
                e.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseUtil handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException occurred: {}", e.getMessage(), e);
        return ResponseUtil.createErrorResponse(
                ErrorConst.SQL_EXCEPTION_CODE,
                ExceptionUtils.getRootCauseMessage(e)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseUtil handleGenericException(Exception e) {
        log.error("Unhandled exception occurred: {}", e.getMessage(), e);
        return ResponseUtil.createErrorResponse(
                ErrorConst.GENERAL_ERROR_CODE,
                ExceptionUtils.getRootCauseMessage(e)
        );
    }
}
