package com.tictactoe.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

/**
 * Base RFC7807 handler. Subclasses or services can extend/use for consistent error shape.
 * Expects exceptions that carry status + errorCode (e.g. custom exceptions) or maps unknown to 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProblemException.class)
    public ResponseEntity<ProblemDetails> handleProblem(ProblemException ex, HttpServletRequest request) {
        UUID correlationId = correlationFromRequestOrMdc(request);
        ProblemDetails body = ProblemDetails.of(
            ex.getStatus(),
            ex.getTitle(),
            ex.getDetail(),
            ex.getErrorCode(),
            request.getRequestURI() != null ? URI.create(request.getRequestURI()) : null,
            correlationId
        );
        return ResponseEntity
            .status(ex.getStatus())
            .contentType(MediaType.parseMediaType("application/problem+json"))
            .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleOther(Exception ex, HttpServletRequest request) {
        UUID correlationId = correlationFromRequestOrMdc(request);
        ProblemDetails body = ProblemDetails.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
            "INTERNAL_ERROR",
            request.getRequestURI() != null ? URI.create(request.getRequestURI()) : null,
            correlationId
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.parseMediaType("application/problem+json"))
            .body(body);
    }

    private static UUID correlationFromRequestOrMdc(HttpServletRequest request) {
        String h = request.getHeader(CorrelationFilter.CORRELATION_ID_HEADER);
        if (h != null && !h.isBlank()) {
            try { return UUID.fromString(h); } catch (Exception ignored) {}
        }
        String mdc = MDC.get(CorrelationFilter.MDC_KEY);
        if (mdc != null && !mdc.isBlank()) {
            try { return UUID.fromString(mdc); } catch (Exception ignored) {}
        }
        return null;
    }
}
