package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.downloader.Downloader;
import com.yilnz.surfing.core.downloader.SurfHttpDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SurfSprider {
	private List<SurfHttpRequest> requests;
	private Downloader downloader;
	private int threadnum;
	private static final Logger logger = LoggerFactory.getLogger(SurfSprider.class);
	private SurfPageProcessor pageProcessor;

	private SurfSprider() {
		this.requests = new ArrayList<>();
	}

	public static SurfSprider create() {
		return new SurfSprider();
	}


	public SurfSprider setRequests(List<SurfHttpRequest> requests) {
		this.requests = requests;
		return this;
	}

	public SurfSprider addRequest(SurfHttpRequest request) {
		if(request.getMethod() == null){
			throw new UnsupportedOperationException("[surfing]request method 不能为空");
		}
		if(request.getUrl() == null){
			throw new UnsupportedOperationException("[surfing]request URL 不能为空");
		}
		this.requests.add(request);
		return this;
	}

	public SurfSprider thread(int threadnum) {
		this.threadnum = threadnum;
		return this;
	}

	public SurfSprider setProcessor(SurfPageProcessor processor) {
		this.pageProcessor = processor;
		return this;
	}

	/**
	 * request sync
	 * @return
	 */
	public Page request(){
		if(requests.size() == 0){
			throw new UnsupportedOperationException("[surfing]没有任何Request,请调用addRequest方法");
		}
		if (downloader == null) {
			downloader = new SurfHttpDownloader(requests, threadnum, null);
		}
		final List<Future<Page>> downloads = downloader.downloads();
		Page page = null;
		try {
			page = downloads.get(0).get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("[surfing]requestSync error", e);
		}
		return page;
	}

	/**
	 * request async
	 */
	public void start() {
		if (downloader == null) {
			downloader = new SurfHttpDownloader(requests, threadnum, pageProcessor);
		}
		downloader.downloads();
	}
}
