package com.yilnz.surfing.core.downloader;

import com.yilnz.surfing.core.Site;
import com.yilnz.surfing.core.SurfHttpClient;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessorInterface;
import com.yilnz.surfing.core.basic.Header;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.plugin.CookieProvider;
import com.yilnz.surfing.core.plugin.ReLogin;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
	private ReLogin reLogin;

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

	public SurfHttpDownloader(List<SurfHttpRequest> requests, int threadNum, SurfPageProcessorInterface pageProcessor, Site site, HttpProxy proxy, ProxyProvider proxyProvider, ReLogin reLogin) {
		this.requests = requests;
		this.threadNum = threadNum;
		if(threadNum <= 1){
			this.threadNum = 1;
		}
		this.pageProcessor = pageProcessor;
		this.site = site;
		this.proxy = proxy;
		this.proxyProvider = proxyProvider;
		this.reLogin = reLogin;
		initComponents();
	}

	private void initComponents(){
		threadPool = Executors.newFixedThreadPool(threadNum);
	}

	public void stopNow(){
		threadPool.shutdownNow();
	}

	private List<Future<Page>> downloads(List<SurfHttpRequest> requests) {
		this.startTime = new Date();
		SurfHttpClient httpClient = new SurfHttpClient();
		httpClient.setProxy(this.proxy);
		httpClient.setProxyProvider(this.proxyProvider);
		List<Future<Page>> pages = new ArrayList<>();
		requests.forEach(e->{
			final Site currentSite = (site == null) ? e.getSite() : site;
			pages.add(threadPool.submit(new Callable<Page>() {
				@Override
				public Page call() {
					Page page = null;
					try {
						page = httpClient.request(e);
						page.setData(e.getData());
						page.setRequest(e);
						if (proxyProvider != null) {
							proxyProvider.pageReturn(page.getUsedProxy(), page);
						}
						page = checkLogin(page);
						if (pageProcessor != null) {
							if (currentSite.getRetryTimes() > 0 && page.getStatusCode() != 200) {
								logger.warn("[surfing]状态码 {}, 重试 {}, 重试次数还剩 {} 次, 消息体 {}", page.getStatusCode(), page.getUrl(), currentSite.getRetryTimes(), page.getHtml().get());
								final List<SurfHttpRequest> retryList = new ArrayList<>();
								retryList.add(e);
								new SurfHttpDownloader(retryList, 1, pageProcessor, Site.me().clone(currentSite).setRetryTimes(currentSite.getRetryTimes() - 1), proxy, proxyProvider, null).downloads();
								retryPageCount.incrementAndGet();
							} else if (page.getStatusCode() == 200) {
								try {
									pageProcessor.process(page);
								} catch (Exception e) {
									logger.error("[surfing]process error", e);
								}
								successPageCount.incrementAndGet();
								totalPageCount.incrementAndGet();
							} else if (currentSite.getRetryTimes() <= 0) {
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
					}catch (Error e){
						logger.error("[surfing]request fatal error", e);
					}
					return page;
				}
			}));
			try {
				Thread.sleep(currentSite.getSleepTime());
			} catch (InterruptedException e1) {
				logger.info("[surfing]sleep error", e1);
			}
		});

		threadPool.shutdown();
		return pages;
	}

	private Page checkLogin(Page page) {
		if (reLogin != null && !reLogin.isLoginSuccess(page)) {
			CookieProvider cookieProvider = reLogin.getCookie(page);
			String cookie = cookieProvider.getCookies();
			logger.info("[surfing]尝试重新登录中...Cookie={},customHeaders={}", cookie, cookieProvider.getCustomHeaders());
			if(cookie == null && cookieProvider.getCustomHeaders().size() == 0){
				logger.info("[surfing]重新登录需要的Cookie为空,或者customHeaders.size()==0，请检查getCookie是否返回正确的值, 强制停止爬虫");
				stopNow();
				return page;
			}
			SurfHttpRequest request = page.getRequest();
			if(cookie != null) {
				request.addHeader("Cookie", cookie);
			}
			for (Header customHeader : cookieProvider.getCustomHeaders()) {
				request.addHeader(customHeader.getName(), customHeader.getValue());
			}
			final List<SurfHttpRequest> retryList = new ArrayList<>();
			retryList.add(request);
			Site site = this.site == null ? request.getSite() : this.site;
			List<Future<Page>> downloads = new SurfHttpDownloader(retryList, 1, pageProcessor, Site.me().clone(site).setRetryTimes(1), proxy, proxyProvider, null).downloads();
			Page page1 = null;
			try {
				page1 = downloads.get(0).get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error("[surfing]登录重上失败",e);
			}
			if(page1 == null || !reLogin.isLoginSuccess(page1)){
				logger.error("[surfing]连续登录失败，强制停止爬虫");
				stopNow();
				return page;
			}
			saveCookie(reLogin.getCookieKey(), request);
			logger.info("[surfing]登录成功，存储Cookie={}", cookie);
			return page1;
		}
		return page;
	}

	public void saveCookie(String key, SurfHttpRequest request) {
		final String tmpDir = System.getProperty("java.io.tmpdir");
		try {
			String base64 = new String(Base64.encodeBase64(request.getUrl().getBytes()));
			if (key != null) {
				base64 = new String(Base64.encodeBase64(key.getBytes()));
			}
			final FileOutputStream fos = new FileOutputStream(new File(tmpDir, base64));
			fos.write(request.getHeaders().entrySet().stream().map(e->e.getKey() + "=" + e.getValue()).collect(Collectors.joining("\n")).getBytes());
			fos.close();
		} catch (IOException e) {
			logger.error("[surfing]save cookie error.", e);
		}
	}

	@Override
	public List<Future<Page>> downloads() {
		return downloads(this.requests);
	}

}
