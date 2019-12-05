package com.yilnz.surfing.core.downloader;

import com.yilnz.surfing.core.Site;
import com.yilnz.surfing.core.SurfHttpClient;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessorInterface;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class SurfHttpDownloader implements Downloader {

	private SurfPageProcessorInterface pageProcessor;
	private int threadNum;
	private ExecutorService threadPool;
	private Site site;
	private Logger logger = LoggerFactory.getLogger(SurfHttpDownloader.class);
	private AtomicInteger totalPageCount = new AtomicInteger(0);
	private AtomicInteger successPageCount = new AtomicInteger(0);
	private AtomicInteger errorPageCount = new AtomicInteger(0);
	private AtomicInteger retryPageCount = new AtomicInteger(0);
	private Date startTime;
	private HttpProxy proxy;
	private ProxyProvider proxyProvider;

	public HttpProxy getProxy() {
		return proxy;
	}

	public void setProxy(HttpProxy proxy) {
		this.proxy = proxy;
	}

	public AtomicInteger getRetryPageCount() {
		return retryPageCount;
	}

	public Date getStartTime() {
		return startTime;
	}

	public AtomicInteger getErrorPageCount() {
		return errorPageCount;
	}

	public AtomicInteger getTotalPageCount() {
		return totalPageCount;
	}

	public AtomicInteger getSuccessPageCount() {
		return successPageCount;
	}

	private List<SurfHttpRequest> requests;

	public SurfHttpDownloader(List<SurfHttpRequest> requests, int threadNum, SurfPageProcessorInterface pageProcessor, Site site, HttpProxy proxy, ProxyProvider proxyProvider) {
		this.requests = requests;
		this.threadNum = threadNum;
		if(threadNum <= 1){
			this.threadNum = 1;
		}
		this.pageProcessor = pageProcessor;
		this.site = site;
		this.proxy = proxy;
		this.proxyProvider = proxyProvider;
		initComponents();
	}

	private void initComponents(){
		threadPool = Executors.newFixedThreadPool(threadNum);
	}

	private List<Future<Page>> downloads(List<SurfHttpRequest> requests) {
		this.startTime = new Date();
		SurfHttpClient httpClient = new SurfHttpClient();
		httpClient.setProxy(this.proxy);
		httpClient.setProxyProvider(this.proxyProvider);
		List<Future<Page>> pages = new ArrayList<>();
		requests.forEach(e->{
			pages.add(threadPool.submit(new Callable<Page>() {
				@Override
				public Page call() {
					Page page = null;
					try {
						page = httpClient.request(e);
						page.setData(e.getData());
						if (proxyProvider != null) {
							proxyProvider.pageReturn(page.getUsedProxy(), page);
						}
						if (pageProcessor != null) {
							if (site.getRetryTimes() > 0 && page.getStatusCode() != 200) {
								logger.warn("[surfing]状态码 {}, 重试 {}, 重试次数还剩 {} 次, 消息体 {}", page.getStatusCode(), page.getUrl(), site.getRetryTimes(), page.getHtml().get());
								final List<SurfHttpRequest> retryList = new ArrayList<>();
								retryList.add(e);
								new SurfHttpDownloader(retryList, 1, pageProcessor, Site.me().clone(site).setRetryTimes(site.getRetryTimes() - 1), proxy, proxyProvider).downloads();
								retryPageCount.incrementAndGet();
							} else if (page.getStatusCode() == 200) {
								try {
									pageProcessor.process(page);
								} catch (Exception e) {
									logger.error("[surfing]process error", e);
								}
								successPageCount.incrementAndGet();
								totalPageCount.incrementAndGet();
							} else if (site.getRetryTimes() <= 0) {
								try {
									pageProcessor.processError(page);
								} catch (Exception e) {
									logger.error("[surfing]processERROR error", e);
								}
								errorPageCount.incrementAndGet();
								totalPageCount.incrementAndGet();
								logger.error("[surfing]重试次数已用尽:状态码 {}, 请求地址 {}, 消息体 {}",page.getStatusCode(), page.getUrl(), page.getHtml().get());
							}
						}
					}catch (Exception e){
						logger.error("[surfing]request error", e);
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
