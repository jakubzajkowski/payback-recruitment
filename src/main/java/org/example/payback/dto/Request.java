package org.example.payback.dto;

public class Request {
    private RequestType type;
    private String content;

    public Request(RequestType type, String content) {
        this.type = type;
        this.content = content;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
