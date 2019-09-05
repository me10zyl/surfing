package com.yilnz.surfing.core;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SurfHttpRequestBuilder {
	private String url;
	private String method;
	private String body;
	private String contentType = "application/x-www-form-urlencoded; charset=UTF-8";
	private Map<String, String> params;

	public SurfHttpRequestBuilder(String url, String method) {
		this.url = url;
		this.method = method;
		this.params = new HashMap<>();
	}

	public SurfHttpRequestBuilder json(Object jsonBody){
		this.body = JSON.toJSONString(jsonBody);
		this.contentType = "application/json; charset=UTF-8";
		return this;
	}

	public SurfHttpRequestBuilder addParams(String name, String value){
		params.put(name, value);
		return this;
	}

	public SurfHttpRequest build(){
		final SurfHttpRequest req = new SurfHttpRequest();
		req.setMethod(this.method);
		req.setUrl(this.url);
		if (this.contentType != null) {
			req.addHeader("Content-Type", this.contentType);
		}
		if(this.params != null){
			final Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
			while(iterator.hasNext()){
				final Map.Entry<String, String> next = iterator.next();
				req.addParams(next.getKey(), next.getValue());
			}
		}
		return req;
	}
}
