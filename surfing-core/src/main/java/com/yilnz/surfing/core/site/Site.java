package com.yilnz.surfing.core.site;

import com.yilnz.surfing.core.log.LogUploader;

import java.util.HashMap;
import java.util.Map;

public class Site {

	private int sleepTime = 50;
	private int retryTimes = 2;
	private Map<String, String> headers = new HashMap<>();
	private LogUploader logUploader;

	@Override
	public String toString() {
		return "sleepTime=" + sleepTime +
				",retryTimes=" + retryTimes;
	}

	private Site(){
		if(GlobalSite.SITE != null) {
			this.sleepTime = GlobalSite.SITE.sleepTime;
			this.retryTimes = GlobalSite.SITE.retryTimes;
			this.headers = GlobalSite.SITE.headers;
			this.logUploader = GlobalSite.SITE.logUploader;
		}
	}

	public static Site me(){
		return new Site();
	}

	public Site clone(Site site){
		this.setRetryTimes(site.getRetryTimes());
		this.setSleepTime(site.getSleepTime());
		return this;
	}

	public LogUploader getLogUploader() {
		if(logUploader == null){
			//空实现防止报错
			return new LogUploader() {

				@Override
				public void uploadLog(String log, Object data, String type) {

				}
			};
		}
		return logUploader;
	}

	public void setLogUploader(LogUploader logUploader) {
		this.logUploader = logUploader;
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
