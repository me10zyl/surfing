package com.yilnz.surfing.core;

import com.yilnz.surfing.core.header.generators.HeaderGenerator;

import java.util.HashMap;
import java.util.Map;

public class SurfHttpRequest {
    private String url;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private Map<String, String> params = new HashMap<>();
    private HeaderGenerator headerGenerator;

    public void setHeaderGenerator(HeaderGenerator headerGenerator) {
        this.headerGenerator = headerGenerator;
    }

    public Map<String, String> getHeaders() {
        Map<String, String> newHeaders = new HashMap<>();
        newHeaders.putAll(headers);
        if(headerGenerator != null){
            newHeaders.putAll(headerGenerator.generateHeaders());
        }
        return newHeaders;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addHeader(String key, String value){
        headers.put(key, value);
    }

    public void addParams(String name, String value){
        headers.put(name, value);
    }
}
