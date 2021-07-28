package com.yilnz.surfing.core.downloader.filedownload;

import java.util.List;
import java.util.Map;

public interface FileDownloadProcessor {

	void downloadFinished(DownloadFile downloadFile);

	String getFileName(String url, Map<String, List<String>> headerFields);
}
