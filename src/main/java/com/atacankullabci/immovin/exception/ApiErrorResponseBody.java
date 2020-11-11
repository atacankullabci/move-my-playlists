package com.atacankullabci.immovin.exception;

import org.springframework.http.HttpStatus;

public class ApiErrorResponseBody {
    private HttpStatus status;
    private String message;

    public ApiErrorResponseBody() {
    }

    public ApiErrorResponseBody(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiErrorResponseBody{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
