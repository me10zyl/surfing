package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.proxy.HttpProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page {
    private Html html;
    private int statusCode;
    private String url;
    private Object data;
    private Map<String, String> headers = new HashMap<>();
    private String rawText;
    private HttpProxy usedProxy;

    /**
     * 内部使用
     * @param usedProxy
     */
    public void _toUseProx(HttpProxy usedProxy) {
        this.usedProxy = usedProxy;
    }

    /*public String getHeadersText(){
        final StringBuilder sb = new StringBuilder();
        for(Header h : headers) {
            sb.append(h.getName()).append(":").append(h.getValue()).append("\n");
        }
        return sb.toString();
    }*/

    public HttpProxy getUsedProxy() {
        return usedProxy;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key){
        String value = this.getHeaders().get(key);
        if (value == null) {
            value = this.getHeaders().get(key.toLowerCase());
        }
        if (value == null) {
            value = this.getHeaders().get(key.toUpperCase());
        }
        return value;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Html getHtml() {
        final Html html = this.html;
        if (html == null) {
            return null;
        }
        html.setUrl(url);
        return html;
    }

    public void setHtml(Html html) {
        this.html = html;
    }

    public boolean isConnectionError(){
        return this.statusCode == 0;
    }

    @Override
    public String toString() {
        return "url=" + url + "，statusCode=" + statusCode;
    }
}
