package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.downloader.Downloader;
import com.yilnz.surfing.core.downloader.SurfHttpDownloader;
import com.yilnz.surfing.core.downloader.filedownload.FileDownloadProcessor;
import com.yilnz.surfing.core.downloader.filedownload.SurfFileDownloader;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.tool.Tool;
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
	private List<Tool> tools = new ArrayList<>();

	private SurfSprider() {
		this.requests = new ArrayList<>();
	}

	public static SurfSprider create() {
		return new SurfSprider();
	}

	public static Page get(String url){
		final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
		surfHttpRequest.setUrl(url);
		surfHttpRequest.setMethod("GET");
		return SurfSprider.create().addRequest(surfHttpRequest).request();
	}

	public static void download(String basePath, int threadnum, FileDownloadProcessor fileDownloadProcessor, String fileNameRegex, String... urls) {
		List<SurfHttpRequest> requests = new ArrayList<>();
		for (int i = 0; i < urls.length; i++) {
			final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
			surfHttpRequest.setUrl(urls[i]);
			requests.add(surfHttpRequest);
		}
		final SurfFileDownloader downloader = new SurfFileDownloader(requests, threadnum, fileDownloadProcessor, fileNameRegex);
		downloader.downloadFiles(basePath);
	}

	public static Page post(String url, String body){
		final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
		surfHttpRequest.setUrl(url);
		surfHttpRequest.setMethod("POST");
		surfHttpRequest.setBody(body);
		return SurfSprider.create().addRequest(surfHttpRequest).request();
	}

	public static Page postJSON(String url, Object jsonObject){
		final SurfHttpRequest post = new SurfHttpRequestBuilder(url, "POST").json(jsonObject).build();
		return SurfSprider.create().addRequest(post).request();
	}

	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}

	public static SurfSprider create(Tool... tool){
		final SurfSprider surfSprider = new SurfSprider();
		List<Tool> tools = new ArrayList<>();
		for (int i = 0; i < tool.length; i++) {
			tools.add(tool[i]);
		}
		surfSprider.setTools(tools);
		return surfSprider;
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
			downloader = new SurfHttpDownloader(requests, threadnum, null, Site.me());
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
		if(requests.size() == 0){
			throw new UnsupportedOperationException("[surfing]没有任何Request,请调用addRequest方法");
		}
		if (downloader == null) {
			downloader = new SurfHttpDownloader(requests, threadnum, pageProcessor, pageProcessor.getSite());
		}
		final List<Future<Page>> pages = downloader.downloads();

		if(this.tools != null){
			this.tools.forEach(e->{
				e.doWork(pageProcessor, pages);
			});
		}
	}
}
