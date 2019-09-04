package com.yilnz.surfing.core.downloader;

import com.yilnz.surfing.core.SurfHttpClient;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.basic.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SurfHttpDownloader implements Downloader {

	private final SurfPageProcessor pageProcessor;
	private int threadNum;
	private ExecutorService threadPool;


	private List<SurfHttpRequest> requests;

	public SurfHttpDownloader(List<SurfHttpRequest> requests, int threadNum, SurfPageProcessor pageProcessor) {
		this.requests = requests;
		this.threadNum = threadNum;
		if(threadNum <= 1){
			this.threadNum = 1;
		}
		this.pageProcessor = pageProcessor;
		initComponents();
	}

	private void initComponents(){
		threadPool = Executors.newFixedThreadPool(threadNum);
	}

	@Override
	public List<Future<Page>> downloads() {
		SurfHttpClient httpClient = new SurfHttpClient();
		List<Future<Page>> pages = new ArrayList<>();
		this.requests.forEach(e->{
			 pages.add(threadPool.submit(new Callable<Page>() {
				@Override
				public Page call() throws Exception {
					final Page request = httpClient.request(e);
					if(pageProcessor != null) {
						pageProcessor.process(request);
					}
					return request;
				}
			}));
		});
		threadPool.shutdown();
		return pages;
	}
}
