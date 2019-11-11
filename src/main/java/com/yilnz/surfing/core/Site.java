package com.yilnz.surfing.core;

import java.util.HashMap;
import java.util.Map;

public class Site {

	private int sleepTime = 50;
	private int retryTimes = 2;
	private Map<String, String> headers = new HashMap<>();

	private Site(){

	}

	public static Site me(){
		return new Site();
	}

	public Site clone(Site site){
		this.setRetryTimes(site.getRetryTimes());
		this.setSleepTime(site.getSleepTime());
		return this;
	}

	public Site setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
		return this;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public Site setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
		return this;
	}

	public Site addHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}
