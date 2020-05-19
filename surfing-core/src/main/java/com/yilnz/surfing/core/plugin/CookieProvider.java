package com.yilnz.surfing.core.plugin;

import com.yilnz.surfing.core.basic.Header;

import java.util.ArrayList;
import java.util.List;

public class CookieProvider {
	private String cookies;

	private List<Header> customHeaders = new ArrayList<>();

	public List<Header> getCustomHeaders() {
		return customHeaders;
	}

	public void setCustomHeaders(List<Header> customHeaders) {
		this.customHeaders = customHeaders;
	}

	public CookieProvider(String cookies) {
		this.cookies = cookies;
	}

	public String getCookies() {
		return cookies;
	}

	public void addCustomHeader(Header header){
		this.customHeaders.add(header);
	}
}
