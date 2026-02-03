package com.tictactoe.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;
import java.util.UUID;

/**
 * RFC7807 Problem Details body. Content-Type: application/problem+json
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetails {
    private URI type;
    private String title;
    private int status;
    private String detail;
    private URI instance;
    private UUID correlationId;
    private String errorCode;

    public ProblemDetails() {}

    public URI getType() { return type; }
    public void setType(URI type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public URI getInstance() { return instance; }
    public void setInstance(URI instance) { this.instance = instance; }
    public UUID getCorrelationId() { return correlationId; }
    public void setCorrelationId(UUID correlationId) { this.correlationId = correlationId; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public static ProblemDetails of(int status, String title, String detail, String errorCode, URI instance, UUID correlationId) {
        ProblemDetails p = new ProblemDetails();
        p.setType(URI.create("about:blank"));
        p.setTitle(title);
        p.setStatus(status);
        p.setDetail(detail);
        p.setErrorCode(errorCode);
        p.setInstance(instance);
        p.setCorrelationId(correlationId);
        return p;
    }
}
