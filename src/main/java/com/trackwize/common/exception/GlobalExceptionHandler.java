package com.trackwize.common.exception;

import com.trackwize.common.constant.ErrorConst;
import com.trackwize.common.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseUtil handleMissingRequestParam(MissingServletRequestParameterException e) {
        log.error("Missing request parameter: {}", e.getMessage(), e);
        String message = String.format("Missing required parameter: '%s'", e.getParameterName());
        return ResponseUtil.createErrorResponse(
                ErrorConst.NOT_EXIST_CODE,
                message
        );
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseUtil handleMissingPathVariable(MissingPathVariableException e) {
        log.error("Missing path variable: {}", e.getMessage(), e);
        String message = String.format("Missing required path variable: '%s'", e.getVariableName());
        return ResponseUtil.createErrorResponse(
                ErrorConst.NOT_EXIST_CODE,
                message
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseUtil handleNoResourceFound(NoResourceFoundException e) {
        log.error("No resource found: {}", e.getMessage(), e);
        return ResponseUtil.createErrorResponse(
                ErrorConst.NOT_EXIST_CODE,
                "Invalid API endpoint or missing path variable."
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
    public ResponseUtil handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException occurred: {}", e.getMessage(), e);
        return ResponseUtil.createErrorResponse(
                ErrorConst.SQL_EXCEPTION_CODE,
                ExceptionUtils.getRootCauseMessage(e)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseUtil handleGenericException(Exception e) {
        log.error("Unhandled exception occurred: {}", e.getMessage(), e);
        return ResponseUtil.createErrorResponse(
                ErrorConst.GENERAL_ERROR_CODE,
                ExceptionUtils.getRootCauseMessage(e)
        );
    }
}
