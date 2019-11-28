package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.downloader.Downloader;
import com.yilnz.surfing.core.downloader.SurfHttpDownloader;
import com.yilnz.surfing.core.downloader.filedownload.DownloadFile;
import com.yilnz.surfing.core.downloader.filedownload.FileDownloadProcessor;
import com.yilnz.surfing.core.downloader.filedownload.SurfFileDownloader;
import com.yilnz.surfing.core.downloader.filedownload.TmpFile;
import com.yilnz.surfing.core.header.generators.ChromeHeaderGenerator;
import com.yilnz.surfing.core.monitor.SpiderHttpStatus;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

	/**
	 * 阻塞型请求 - GET
	 * @param url 请求地址
	 * @return
	 */
	public static Page get(String url){
		final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
		surfHttpRequest.setUrl(url);
		surfHttpRequest.setMethod("GET");
		return SurfSprider.create().addRequest(surfHttpRequest).request();
	}

	/**
	 * 非阻塞型请求 - 多线程批量下载文件
	 * @param basePath 文件下载目录
	 * @param threadnum 最大线程数
	 * @param fileDownloadProcessor 文件下载完成回调
	 * @param fileNameRegex 文件下载正则，匹配URL，如 [^/]+?(?=/$|$|\?)
	 * @param urls 多个请求地址
	 */
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

	public static File downloadIfNotExist(String filePath, String url){
		return downloadIfNotExist(filePath, url, null);
	}

	public static File download(String filePath, String url){
		return download(filePath, url, null);
	}

	/**
	 * 阻塞型请求 - 在文件不存在的时候下载  例子. downloadIfNotExist("/tmp/" + FileUtil.getFileNameByUrl(url), url)
	 * @param filePath 文件下载路径
	 * @param url 请求地址
	 * @return
	 */
	public static File downloadIfNotExist(String filePath, String url, Site site){
		final File file = new File(filePath);
		if(file.exists()){
			return file;
		}
		return download(filePath, url, site);
	}

	/**
	 * 阻塞型请求 - 下载文件 例子. download("/tmp/" + FileUtil.getFileNameByUrl(url), url)
	 * @param filePath 文件下载路径
	 * @param url 请求地址
	 * @return
	 */
	public static File download(String filePath, String url, Site site) {
		List<SurfHttpRequest> requests = new ArrayList<>();
		final SurfHttpRequest surfHttpRequest = new SurfHttpRequest(site);
		surfHttpRequest.setUrl(url);
		requests.add(surfHttpRequest);
		final SurfFileDownloader downloader = new SurfFileDownloader(requests, filePath);
		DownloadFile downloadFile = null;
		try {
			downloadFile = downloader.downloadFiles(filePath).get(0).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		if (downloadFile == null) {
			return null;
		}
		return new File(downloadFile.getFilename());
	}




	/**
	 * 阻塞型请求 - POST
	 * @param url 请求地址
	 * @param body 请求体
	 * @return
	 */
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
	 * 阻塞型请求 - 开始爬取
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
	 * 非阻塞型请求 - 开始爬取
	 */
	public void start() {
		if(requests.size() == 0){
			throw new UnsupportedOperationException("[surfing]没有任何Request,请调用addRequest方法");
		}
		if (downloader == null) {
			downloader = new SurfHttpDownloader(requests, threadnum, pageProcessor, pageProcessor.getSite());

			//JMX监控
			try {
				MBeanServer server = ManagementFactory.getPlatformMBeanServer();
				ObjectName name = new ObjectName(String.format("spiderHttpStatusMBean-%s:name=spiderHttpStatus", this.hashCode()));
				server.registerMBean(new SpiderHttpStatus((SurfHttpDownloader) downloader), name);
			} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
				logger.error("[surfing]register jmx monitor error", e);
			}

		}
		final List<Future<Page>> pages = downloader.downloads();

		if(this.tools != null){
			this.tools.forEach(e->{
				e.doWork(pageProcessor, pages);
			});
		}
	}
}
