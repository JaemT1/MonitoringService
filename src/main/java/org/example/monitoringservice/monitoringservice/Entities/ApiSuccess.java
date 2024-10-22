package org.example.monitoringservice.monitoringservice.Entities;

import lombok.Data;

@Data
public class ApiSuccess {
    private int status;
    private String success;
    private String message;
    private String path;

    // Constructor, getters y setters

    public ApiSuccess(int status, String success, String message, String path) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.path = path;
    }
}
