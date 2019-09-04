package com.yilnz.surfing.core;

public class Site {

	private int sleepTime = 50;
	private int retryTimes = 2;

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
}
