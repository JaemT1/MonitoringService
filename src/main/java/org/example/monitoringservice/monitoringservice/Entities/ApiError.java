package org.example.monitoringservice.monitoringservice.Entities;

import lombok.Data;

@Data
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;

    // Constructor, getters y setters

    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}

