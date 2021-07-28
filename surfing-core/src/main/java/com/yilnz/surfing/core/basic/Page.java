package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.proxy.HttpProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Page {
    private Html html;
    private int statusCode;
    private String url;
    private Object data;
    private List<Header> headers = new ArrayList<>();
    private String rawText;
    private HttpProxy usedProxy;
    private SurfHttpRequest request;


    public String getCookie(){
		List<Header> headers = this.getHeaders("Set-Cookie");
		return headers.stream().map(e-> e.getValue().split(";")[0]).collect(Collectors.joining(";"));
	}

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

	public SurfHttpRequest getRequest() {
		return request;
	}

	public void setRequest(SurfHttpRequest request) {
		this.request = request;
	}

	public HttpProxy getUsedProxy() {
        return usedProxy;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public String getHeader(String key){
		List<Header> headers = getHeaders(key);
		if(headers == null || headers.size() == 0){
			return null;
		}
		return headers.get(0).getValue();
    }

	public List<Header> getHeaders(String key){
		return this.getHeaders().stream().filter(e->{
			return key.equals(e.getName()) || key.toLowerCase().equals(e.getName()) || key.toUpperCase().equals(e.getName());
		}).collect(Collectors.toList());
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
