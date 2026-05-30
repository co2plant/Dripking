package kr.co.inntavern.dripking.service;

import org.springframework.http.HttpStatus;

public class ContentImageUploadException extends RuntimeException {
    private final HttpStatus status;

    public ContentImageUploadException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ContentImageUploadException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
