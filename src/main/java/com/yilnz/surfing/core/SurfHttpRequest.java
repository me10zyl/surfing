package com.yilnz.surfing.core;

import com.yilnz.surfing.core.header.generators.HeaderGenerator;

import java.util.HashMap;
import java.util.Map;

public class SurfHttpRequest {
    private String method;
    private String url;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private Map<String, String> params = new HashMap<>();
    private Map<String, String> bodyParams = new HashMap<>();
    private HeaderGenerator headerGenerator;
    private Object data;

    public String getHeadersString(){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getBodyParams() {
        return bodyParams;
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

    public String getFullUrl(){
        if(getUrl().startsWith("//")){
            return "http:" + getUrl();
        }
        return getUrl();
    }

    public void addHeader(String key, String value){
        headers.put(key, value);
    }

    public void addParams(String name, String value){
        params.put(name, value);
    }

    public void addBodyParams(String name, String value){
        bodyParams.put(name, value);
    }
}
