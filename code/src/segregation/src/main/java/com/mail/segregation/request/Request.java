package com.mail.segregation.request;

import java.util.Map;

public class Request {
    private Map<String,String> content;

    public Map<String, String> getContent() {
        return content;
    }

    public void setContent(Map<String, String> content) {
        this.content = content;
    }
}
