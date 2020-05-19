package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.downloader.Downloader;
import com.yilnz.surfing.core.downloader.SurfHttpDownloader;
import com.yilnz.surfing.core.downloader.filedownload.DownloadFile;
import com.yilnz.surfing.core.downloader.filedownload.FileDownloadProcessor;
import com.yilnz.surfing.core.downloader.filedownload.SurfFileDownloader;
import com.yilnz.surfing.core.monitor.SpiderHttpStatus;
import com.yilnz.surfing.core.plugin.ReLogin;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import com.yilnz.surfing.core.tool.Tool;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SurfSpider {
	private List<SurfHttpRequest> requests;
	private Downloader downloader;
	private int threadnum;
	private static final Logger logger = LoggerFactory.getLogger(SurfSpider.class);
	private SurfPageProcessor pageProcessor;
	private List<Tool> tools = new ArrayList<>();
	private HttpProxy proxy;
	private ProxyProvider proxyProvider;
	private Site site = Site.me();
	private ReLogin reLogin;

	public ProxyProvider getProxyProvider() {
		return proxyProvider;
	}

	public SurfSpider setProxyProvider(ProxyProvider proxyProvider) {
		this.proxyProvider = proxyProvider;
		return this;
	}

	public HttpProxy getProxy() {
		return proxy;
	}

	public SurfSpider setProxy(HttpProxy proxy) {
		this.proxy = proxy;
		return this;
	}

	private SurfSpider() {
		this.requests = new ArrayList<>();
	}

	public static SurfSpider create() {
		return new SurfSpider();
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
		return SurfSpider.create().addRequest(surfHttpRequest).request().get(0);
	}

	/**
	 * 阻塞型请求 - GET
	 * @return
	 */
	public static Page getWithLogin(String url, ReLogin reLogin){
		final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
		surfHttpRequest.setUrl(url);
		surfHttpRequest.setMethod("GET");
		SurfSpider surfSpider = SurfSpider.create();
		return surfSpider.addRequest(surfHttpRequest).tryLoginWhenFailed(reLogin).request().get(0);
	}

	/**
	 * 阻塞型请求 - GET
	 *
	 * @param request the request
	 * @return the page
	 */
	public static Page get(SurfHttpRequest request) {
		request.setMethod("GET");
		return SurfSpider.create().addRequest(request).request().get(0);
	}

	/**
	 * 非阻塞型请求 - 多线程批量下载文件
	 * @param basePath 文件下载目录
	 * @param threadnum 最大线程数
	 * @param fileDownloadProcessor 文件下载完成回调
	 * @param fileNameRegex 文件下载正则，匹配URL，如 [^/]+?(?=/$|$|\?)
	 * @param urls 多个请求地址
	 */
	public static void getPage(String basePath, int threadnum, FileDownloadProcessor fileDownloadProcessor, String fileNameRegex, String... urls) {
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

	public static File getPage(String filePath, String url){
		return getPage(filePath, url, null);
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
		return getPage(filePath, url, site);
	}

	/**
	 * 阻塞型请求 - 下载文件 例子. getPage("/tmp/" + FileUtil.getFileNameByUrl(url), url)
	 * @param filePath 文件下载路径
	 * @param url 请求地址
	 * @return
	 */
	public static File getPage(String filePath, String url, Site site) {
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
		return SurfSpider.create().addRequest(surfHttpRequest).request().get(0);
	}

	public static Page postJSON(String url, Object jsonObject){
		final SurfHttpRequest post = new SurfHttpRequestBuilder(url, "POST").json(jsonObject).build();
		return SurfSpider.create().addRequest(post).request().get(0);
	}

	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}

	public static SurfSpider create(Tool... tool){
		final SurfSpider surfSprider = new SurfSpider();
		List<Tool> tools = new ArrayList<>();
		for (int i = 0; i < tool.length; i++) {
			tools.add(tool[i]);
		}
		surfSprider.setTools(tools);
		return surfSprider;
	}


	public SurfSpider setRequests(List<SurfHttpRequest> requests) {
		this.requests = requests;
		return this;
	}

	public List<SurfHttpRequest> getRequests() {
		return this.requests;
	}

	public SurfSpider addRequest(SurfHttpRequest request) {
		if(request.getMethod() == null){
			throw new UnsupportedOperationException("[surfing]request method 不能为空");
		}
		if(request.getUrl() == null){
			throw new UnsupportedOperationException("[surfing]request URL 不能为空");
		}
		this.requests.add(request);
		return this;
	}

	public SurfSpider thread(int threadnum) {
		this.threadnum = threadnum;
		return this;
	}

	public SurfSpider setProcessor(SurfPageProcessor processor) {
		this.pageProcessor = processor;
		return this;
	}



	private Page getPage(Downloader downloader) {
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
	 * 重试
	 * @return
	 */
	public Page retry(){
		if(requests.size() == 0){
			throw new UnsupportedOperationException("[surfing]没有任何Request,请调用addRequest方法");
		}
		Downloader downloader = new SurfHttpDownloader(requests, threadnum, null, Site.me(), this.proxy, this.proxyProvider, this.reLogin);

		return getPage(downloader);
	}

	/**
	 * 阻塞型请求 - 开始爬取
	 * @return
	 */
	public List<Page> request(){
		final List<Future<Page>> start = start();
		List<Page> pages = new ArrayList<>();
		for (Future<Page> download : start) {
			try {
				pages.add(download.get());
			} catch (InterruptedException | ExecutionException e) {
				logger.error("[surfing]requestSync error", e);
			}
		}
		return pages;
	}

	public Site getSite() {
		return site;
	}

	public SurfSpider setSite(Site site) {
		this.site = site;
		return this;
	}

	public void stopNow(){
		if(downloader == null){
			return;
		}
		if(downloader instanceof SurfHttpDownloader){
			logger.info("[surfing]立刻停止继续爬取数据");
			((SurfHttpDownloader)downloader).stopNow();
			downloader = null;
		}else{
			logger.warn("[surfing]只有Http请求才能立刻停止");
		}
	}

	/**
	 * 非阻塞型请求 - 开始爬取
	 */
	public List<Future<Page>> start() {
		if(requests.size() == 0){
			throw new UnsupportedOperationException("[surfing]没有任何Request,请调用addRequest方法");
		}
		if (downloader == null) {
			downloader = new SurfHttpDownloader(requests, threadnum, pageProcessor, site, this.proxy, this.proxyProvider, this.reLogin);

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

		return pages;
	}

	public SurfSpider tryLoginWhenFailed(ReLogin reLogin){
		this.reLogin = reLogin;
		if (this.requests == null || this.requests.size() == 0) {
			throw new UnsupportedOperationException("[surfing]must add request first before invoking tryLoginWhenFailed");
		}
		loadCookie(reLogin.getCookieKey());
		return this;
	}

	public SurfSpider loadCookie(String key){
		if (this.requests == null || this.requests.size() == 0) {
			throw new UnsupportedOperationException("[surfing]must add request first when load cookie");
		}
		logger.info("[surfing]（本地缓存）从本地读取cookieKey={}用于登录", key);
		final String tmpDir = System.getProperty("java.io.tmpdir");
		for (SurfHttpRequest request : this.requests) {
			String base64 = new String(Base64.encodeBase64(request.getUrl().getBytes()));
			if (key != null) {
				base64 = new String(Base64.encodeBase64(key.getBytes()));
			}
			try {
				byte[] bytes = Files.readAllBytes(Paths.get(tmpDir, base64));
				StringTokenizer tokenizer = new StringTokenizer(new String(bytes, "UTF-8"), "\n");
				while (tokenizer.hasMoreElements()){
					String line = (String) tokenizer.nextElement();
					int indexOfEq = line.indexOf("=");
					String name = line.substring(0, indexOfEq);
					String value = line.substring(indexOfEq + 1);
					request.addHeader(name, value);
				}
			} catch (IOException e) {
				//logger.error("[surfing]load cookie error.", e);
			}
		}
		return this;
	}

	/*public void saveCookie(String key) {
		final String tmpDir = System.getProperty("java.io.tmpdir");
		for (SurfHttpRequest request : this.requests) {
			try {
				String base64 = new String(Base64.encodeBase64(request.getUrl().getBytes()));
				if (key != null) {
					base64 = new String(Base64.encodeBase64(key.getBytes()));
				}
				final FileOutputStream fos = new FileOutputStream(new File(tmpDir, base64));
				fos.write(request.getHeaders().get("Cookie").getBytes());
				fos.close();
			} catch (IOException e) {
				logger.error("[surfing]save cookie error.", e);
			}
		}

	}*/
}
