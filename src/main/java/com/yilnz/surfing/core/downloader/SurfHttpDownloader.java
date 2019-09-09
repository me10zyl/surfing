package com.yilnz.surfing.core.downloader;

import com.yilnz.surfing.core.Site;
import com.yilnz.surfing.core.SurfHttpClient;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessorInterface;
import com.yilnz.surfing.core.basic.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SurfHttpDownloader implements Downloader {

	private SurfPageProcessorInterface pageProcessor;
	private int threadNum;
	private ExecutorService threadPool;
	private Site site;
	private Logger logger = LoggerFactory.getLogger(SurfHttpDownloader.class);


	private List<SurfHttpRequest> requests;

	public SurfHttpDownloader(List<SurfHttpRequest> requests, int threadNum, SurfPageProcessorInterface pageProcessor, Site site) {
		this.requests = requests;
		this.threadNum = threadNum;
		if(threadNum <= 1){
			this.threadNum = 1;
		}
		this.pageProcessor = pageProcessor;
		this.site = site;
		initComponents();
	}

	private void initComponents(){
		threadPool = Executors.newFixedThreadPool(threadNum);
	}

	private List<Future<Page>> downloads(List<SurfHttpRequest> requests) {
		SurfHttpClient httpClient = new SurfHttpClient();
		List<Future<Page>> pages = new ArrayList<>();
		requests.forEach(e->{
			pages.add(threadPool.submit(new Callable<Page>() {
				@Override
				public Page call() throws Exception {
					final Page page = httpClient.request(e);
					page.setData(e.getData());
					if(site.getRetryTimes() > 0 && page.getStatusCode() != 200){
						final List<SurfHttpRequest> retryList = new ArrayList<>();
						retryList.add(e);
						new SurfHttpDownloader(retryList, 1, pageProcessor, Site.me().clone(site).setRetryTimes(site.getRetryTimes()-1));
					}
					if(pageProcessor != null && page.getStatusCode() == 200) {
						pageProcessor.process(page);
					}

					if(site.getRetryTimes() <= 0){
						pageProcessor.processError(page);
					}
					return page;
				}
			}));
			try {
				Thread.sleep(site.getSleepTime());
			} catch (InterruptedException e1) {
				logger.info("[surfing]sleep error", e1);
			}
		});

		threadPool.shutdown();
		return pages;
	}

	@Override
	public List<Future<Page>> downloads() {
		return downloads(this.requests);
	}

}
