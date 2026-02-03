package com.tictactoe.common;

public class ProblemException extends RuntimeException {
    private final int status;
    private final String title;
    private final String detail;
    private final String errorCode;

    public ProblemException(int status, String title, String detail, String errorCode) {
        super(detail);
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorCode = errorCode;
    }

    public int getStatus() { return status; }
    public String getTitle() { return title; }
    public String getDetail() { return detail; }
    public String getErrorCode() { return errorCode; }
}
