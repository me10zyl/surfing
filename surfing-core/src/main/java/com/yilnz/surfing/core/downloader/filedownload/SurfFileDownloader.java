package com.yilnz.surfing.core.downloader.filedownload;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.downloader.Downloader;
import com.yilnz.surfing.core.downloader.SurfHttpDownloader;
import com.yilnz.surfing.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SurfFileDownloader implements Downloader {

	private String fileName;
	private FileDownloadProcessor fileDownloadProcessor;
	private int threadNum;
	private ExecutorService threadPool;
	private Logger logger = LoggerFactory.getLogger(SurfHttpDownloader.class);


	private List<SurfHttpRequest> requests;

	public SurfFileDownloader(List<SurfHttpRequest> requests, int threadnum, FileDownloadProcessor fileDownloadProcessor) {
		this.requests = requests;
		this.fileDownloadProcessor = fileDownloadProcessor;
		if(threadNum <= 1){
			this.threadNum = 1;
		}
		this.threadNum = threadnum;
		initComponents();
	}

	public SurfFileDownloader(List<SurfHttpRequest> requests, String fileName) {
		this.requests = requests;
		this.threadNum = 1;
		this.fileName = fileName;
		initComponents();
	}

	private void initComponents(){
		threadPool = Executors.newFixedThreadPool(threadNum);
	}


	public List<Future<DownloadFile>> downloadFiles(String basePath){
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};

		// Activate the new trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}

		List<Future<DownloadFile>> futures = new ArrayList<>();
		if(!basePath.endsWith("/")){
			basePath += "/";
		}
		String finalBasePath = basePath;
		this.requests.forEach(e->{
			final Future<DownloadFile> submit = threadPool.submit(() -> {
				InputStream in = null;
				String filepath = null;
				final URL url = new URL(e.getFullUrl());
				final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				try {
					final Map<String, String> headers = e.getHeaders();
					for (Map.Entry<String, String> entry : headers.entrySet()) {
						urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
					}
					in = urlConnection.getInputStream();
					if(fileName == null) {
						filepath = Paths.get(finalBasePath ,FileUtil.getFileNameByUrl(e.getUrl())).toString();
					}else{
						filepath = Paths.get(finalBasePath,fileName).toString();
					}
					if(fileDownloadProcessor != null) {
						String newName = fileDownloadProcessor.getFileName(e.getUrl(), urlConnection.getHeaderFields());
						filepath = newName == null ? filepath : (Paths.get(finalBasePath,newName).toString());
					}
					Files.copy(in, Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e1) {
					logger.error("[surfing]getPage error #1", e1);
					final InputStream errorStream = urlConnection.getErrorStream();
					byte[] buffer = new byte[1024];
					int len = -1;
					StringBuilder sb = new StringBuilder();
					while ((len = errorStream.read(buffer)) > 0) {
						sb.append(new String(buffer, 0, len));
					}
					errorStream.close();
					logger.error("[surfing]getPage error #2 {}", sb.toString());
				}finally {
					if (in != null) {
						in.close();
					}
					urlConnection.disconnect();
				}
				final DownloadFile downloadFile = new DownloadFile();
				downloadFile.setUrl(e.getFullUrl());
				downloadFile.setFilepath(filepath);
				if (fileDownloadProcessor != null) {
					fileDownloadProcessor.downloadFinished(downloadFile);
				}
				return downloadFile;
			});
			futures.add(submit);
		});

		threadPool.shutdown();
		return futures;
	}

	@Override
	public List<Future<Page>> downloads() {
		return null;
	}
}
