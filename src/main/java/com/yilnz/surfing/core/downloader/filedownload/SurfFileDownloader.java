package com.yilnz.surfing.core.downloader.filedownload;

import com.yilnz.surfing.core.Site;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessorInterface;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.downloader.Downloader;
import com.yilnz.surfing.core.downloader.SurfHttpDownloader;
import com.yilnz.surfing.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SurfFileDownloader implements Downloader {

	private String fileName;
	private FileDownloadProcessor fileDownloadProcessor;
	private int threadNum;
	private ExecutorService threadPool;
	private Logger logger = LoggerFactory.getLogger(SurfHttpDownloader.class);
	private String fileNameRegex;


	private List<SurfHttpRequest> requests;

	public SurfFileDownloader(List<SurfHttpRequest> requests, int threadnum, FileDownloadProcessor fileDownloadProcessor, String fileNameRegex) {
		this.requests = requests;
		this.fileDownloadProcessor = fileDownloadProcessor;
		if(threadNum <= 1){
			this.threadNum = 1;
		}
		this.threadNum = threadnum;
		this.fileNameRegex = fileNameRegex;
		initComponents();
	}

	public SurfFileDownloader(List<SurfHttpRequest> requests, String fileName) {
		this.requests = requests;
		this.threadNum = 1;
		this.fileName = fileName;
		initComponents();
	}

	public SurfFileDownloader(List<SurfHttpRequest> requests, int threadnum, FileDownloadProcessor fileDownloadProcessor) {
		this(requests, threadnum, fileDownloadProcessor, null);
	}

	private void initComponents(){
		threadPool = Executors.newFixedThreadPool(threadNum);
	}


	public List<Future<DownloadFile>> downloadFiles(String basePath){
		List<Future<DownloadFile>> futures = new ArrayList<>();
		if(!basePath.endsWith("/")){
			basePath += "/";
		}
		String finalBasePath = basePath;
		this.requests.forEach(e->{
			final Future<DownloadFile> submit = threadPool.submit(() -> {
				InputStream in;
				String filepath = null;
				try {
					in = new URL(e.getFullUrl()).openStream();

					if(fileName == null) {
						if (fileNameRegex != null) {
							filepath = finalBasePath + FileUtil.getFileNameByUrl(e.getUrl(), fileNameRegex);
						} else {
							filepath = finalBasePath + FileUtil.getFileNameByUrl(e.getUrl());
						}
					}else{
						filepath = fileName;
					}
					Files.copy(in, Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e1) {
					logger.error("[surfing]download error", e1);
				}
				final DownloadFile downloadFile = new DownloadFile();
				downloadFile.setUrl(e.getFullUrl());
				downloadFile.setFilename(filepath);
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
