package com.ulp.eventapp.model;

public class ApiResponseMessage {
    private String message;

    public ApiResponseMessage() {
    }

    public ApiResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiResponseMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
