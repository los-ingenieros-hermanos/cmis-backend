package com.los.cmisbackend.payload.request;

public class MessageRequest {

    private String message = "none";

    public MessageRequest() {

    }

    public MessageRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
