package org.inamsay.net.micromoneyexchange.exception;

import lombok.Data;

import java.time.Instant;

/**
 * ErrorResponse class represents the structure of error responses returned by the API.
 * It includes fields for timestamp, status, error, message, and path.
 */
@Data
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
