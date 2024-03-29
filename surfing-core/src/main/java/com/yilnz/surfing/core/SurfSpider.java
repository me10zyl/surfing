package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Header;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.downloader.Downloader;
import com.yilnz.surfing.core.downloader.SurfHttpDownloader;
import com.yilnz.surfing.core.downloader.filedownload.DownloadFile;
import com.yilnz.surfing.core.downloader.filedownload.FileDownloadProcessor;
import com.yilnz.surfing.core.downloader.filedownload.SurfFileDownloader;
import com.yilnz.surfing.core.monitor.SpiderHttpStatus;
import com.yilnz.surfing.core.plugin.PaginationClz;
import com.yilnz.surfing.core.plugin.ReLogin;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import com.yilnz.surfing.core.site.Site;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SurfSpider {
	private List<SurfHttpRequest> requests;
	private Downloader downloader;
	private int threadnum;
	private static final Logger logger = LoggerFactory.getLogger(SurfSpider.class);
	private SurfPageProcessor pageProcessor;
	private HttpProxy proxy;
	private ProxyProvider proxyProvider;
	private Site site = Site.me();
	private ReLogin reLogin;
	private List<Header> defaultRequestHeaders;

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
	 *
	 * @param url 请求地址
	 * @return Page
	 */
	public static Page get(String url) {
		final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
		surfHttpRequest.setUrl(url);
		surfHttpRequest.setMethod("GET");
		return SurfSpider.create().addRequest(surfHttpRequest).request().get(0);
	}

	/**
	 * 阻塞型请求 - GET
	 *
	 * @param url 地址
	 * @param reLogin 重登请求
	 * @return Page
	 */
	public static Page getWithLogin(String url, ReLogin reLogin) {
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
	 *
	 * @param basePath              文件下载目录
	 * @param threadnum             最大线程数
	 * @param fileDownloadProcessor 文件下载完成回调
	 * @param urls                  多个请求地址
	 * @return 下载的页面
	 */
	public static List<Future<DownloadFile>> downloadBatch(String basePath, int threadnum, FileDownloadProcessor fileDownloadProcessor, String... urls) {
		List<SurfHttpRequest> requests = new ArrayList<>();
		for (int i = 0; i < urls.length; i++) {
			final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
			surfHttpRequest.setUrl(urls[i]);
			requests.add(surfHttpRequest);
		}
		final SurfFileDownloader downloader = new SurfFileDownloader(requests, threadnum, fileDownloadProcessor);
		List<Future<DownloadFile>> futures = downloader.downloadFiles(basePath);
		return futures;
	}

	/**
	 *
	 * 阻塞型请求 - 在文件不存在的时候下载  例子. downloadIfNotExist("/tmp/" + FileUtil.getFileNameByUrl(url), url)
	 *
	 * @param filePath 文件下载路径
	 * @param url      请求地址
	 * @return {@link File}
	 */
	public static File downloadIfNotExist(String filePath, String url) {
		final File file = new File(filePath);
		if (file.exists()) {
			return file;
		}
		return download(filePath, url);
	}

	/**
	 *
	 * 阻塞型请求 - 下载文件 例子. getPage("/tmp/" + FileUtil.getFileNameByUrl(url), url)
	 *
	 * @param filePath 文件下载路径
	 * @param url      请求地址
	 * @return {@link File}
	 */
	public static File download(String filePath, String url) {
		final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
		surfHttpRequest.setUrl(url);
		return download(filePath, surfHttpRequest);
	}


	/**
	 * 下载
	 *
	 * @param filePath 文件路径
	 * @param request  请求
	 * @return {@link File}
	 */
	public static File download(String filePath, SurfHttpRequest request) {
		List<SurfHttpRequest> requests = new ArrayList<>();
		requests.add(request);
		Path path = Paths.get(filePath);
		Path fileName = path.getFileName();
		final SurfFileDownloader downloader = new SurfFileDownloader(requests, fileName.toString());
		DownloadFile downloadFile = null;
		try {
			downloadFile = downloader.downloadFiles(path.getParent().toString()).get(0).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		if (downloadFile == null) {
			return null;
		}
		return new File(downloadFile.getFilepath());
	}


	/**
	 *
	 * 阻塞型请求 - POST
	 *
	 * @param url  请求地址
	 * @param body 请求体
	 * @return {@link Page}
	 */
	public static Page post(String url, String body) {
		final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
		surfHttpRequest.setUrl(url);
		surfHttpRequest.setMethod("POST");
		surfHttpRequest.setBody(body);
		return SurfSpider.create().addRequest(surfHttpRequest).request().get(0);
	}

	/**
	 *
	 * 阻塞型请求 - postJSON
	 *
	 * @param url        url
	 * @param jsonObject json对象
	 * @return {@link Page}
	 */
	public static Page postJSON(String url, Object jsonObject) {
		final SurfHttpRequest post = new SurfHttpRequestBuilder(url, "POST").json(jsonObject).build();
		return SurfSpider.create().addRequest(post).request().get(0);
	}


	public SurfSpider setRequests(List<SurfHttpRequest> requests) {
		this.requests = requests;
		return this;
	}

	public List<SurfHttpRequest> getRequests() {
		return this.requests;
	}

	public SurfSpider addRequest(SurfHttpRequest request) {
		if (request.getMethod() == null) {
			throw new UnsupportedOperationException("[surfing]request method 不能为空");
		}
		if (request.getUrl() == null) {
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


	private Page downloadPage(Downloader downloader) {
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
	 *
	 * @return {@link Page}
	 */
	public Page retry() {
		if (requests.size() == 0) {
			throw new UnsupportedOperationException("[surfing]没有任何Request,请调用addRequest方法");
		}
		Downloader downloader = new SurfHttpDownloader(requests, threadnum, null, Site.me(), this.proxy, this.proxyProvider, this.reLogin);

		return downloadPage(downloader);
	}

	/**
	 *
	 * 阻塞型请求 - 开始爬取
	 *
	 * @return 页面
	 */
	public List<Page> request() {
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

	public void stopNow() {
		if (downloader == null) {
			return;
		}
		if (downloader instanceof SurfHttpDownloader) {
			logger.info("[surfing]立刻停止继续爬取数据");
			((SurfHttpDownloader) downloader).stopNow();
			downloader = null;
		} else {
			logger.warn("[surfing]只有Http请求才能立刻停止");
		}
	}

	/**
	 * 非阻塞型请求 - 开始爬取
	 * @return 页面
	 */
	public List<Future<Page>> start() {
		if (requests.size() == 0) {
			throw new UnsupportedOperationException("[surfing]没有任何Request,请调用addRequest方法");
		}
		//设置默认HeaderList
		if (this.defaultRequestHeaders != null) {
			requests.forEach(e->{
				this.defaultRequestHeaders.forEach(ee->{
					if(!e.getHeaders().containsKey(ee.getName())) {
						e.addHeader(ee.getName(), ee.getValue());
					}
				});

			});
		}

		if (downloader == null) {
			downloader = new SurfHttpDownloader(requests, threadnum, pageProcessor, site, this.proxy, this.proxyProvider, this.reLogin);

			//JMX监控 temporary removed
			/*try {
				MBeanServer server = ManagementFactory.getPlatformMBeanServer();
				ObjectName name = new ObjectName(String.format("spiderHttpStatusMBean-%s:name=spiderHttpStatus", this.hashCode()));
				server.registerMBean(new SpiderHttpStatus((SurfHttpDownloader) downloader), name);
			} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
				logger.error("[surfing]register jmx monitor error", e);
			}*/

		}
		final List<Future<Page>> pages = downloader.downloads();

		return pages;
	}

	public SpiderHttpStatus getHttpStatus(){
		return new SpiderHttpStatus((SurfHttpDownloader) downloader);
	}


	public static List<Future<Page>> startPagination(PaginationClz clz) {
		int pageCount = clz.getPageCount();
		logger.info("[surfing]获取总页数 " + pageCount + " ");
		SurfSpider surfSpider = clz.surfSpider();
		for (int i = 1; i <= pageCount; i++) {
			SurfHttpRequest pageUrl = clz.getPageUrl(i);
			pageUrl.setData(i);
			surfSpider.addRequest(pageUrl);
		}
		if(surfSpider.threadnum == 0){
			surfSpider.thread(5);
		}
		return surfSpider.setProcessor(new SurfPageProcessor() {
			@Override
			public void process(Page page) {
				clz.handlePage().process(page, (Integer) page.getData());
			}

			@Override
			public void processError(Page page) {
				clz.handlePage().processError(page, (Integer) page.getData());
			}
		}).start();
	}

	public SurfSpider tryLoginWhenFailed(ReLogin reLogin) {
		this.reLogin = reLogin;
		/*if (this.requests == null || this.requests.size() == 0) {
			throw new UnsupportedOperationException("[surfing]must add request first before invoking tryLoginWhenFailed");
		}*/
		loadCookie(reLogin.getCookieKey());
		return this;
	}

	public void setDefaultRequestHeaders(List<Header> headerList) {
		defaultRequestHeaders = headerList;
	}

	public SurfSpider loadCookie(String key) {
		/*if (this.requests == null || this.requests.size() == 0) {
			throw new UnsupportedOperationException("[surfing]must add request first when load cookie");
		}*/
		final String tmpDir = System.getProperty("java.io.tmpdir");

		//for (SurfHttpRequest request : this.requests) {
		//String base64 = new String(Base64.encodeBase64(request.getUrl().getBytes()));
		//if (key != null) {
		String base64 = new String(Base64.encodeBase64(key.getBytes()));
		//}
		try {
			Path path = Paths.get(tmpDir, base64);
			logger.info("[surfing]（本地缓存）从本地读取cookieKey={}用于登录，path={}", key, path);
			byte[] bytes = Files.readAllBytes(path);
			StringTokenizer tokenizer = new StringTokenizer(new String(bytes, "UTF-8"), "\n");
			List<Header> defaultHeaderList = new ArrayList<>();
			while (tokenizer.hasMoreElements()) {
				String line = (String) tokenizer.nextElement();
				int indexOfEq = line.indexOf("=");
				String name = line.substring(0, indexOfEq);
				String value = line.substring(indexOfEq + 1);
				defaultHeaderList.add(new Header(name, value));
			}
			setDefaultRequestHeaders(defaultHeaderList);
		} catch (IOException e) {
			//logger.error("[surfing]load cookie error.", e);
		}
		//}
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
