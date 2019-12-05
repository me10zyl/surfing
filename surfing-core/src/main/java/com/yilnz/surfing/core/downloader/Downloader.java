package com.yilnz.surfing.core.downloader;

import com.yilnz.surfing.core.basic.Page;

import java.util.List;
import java.util.concurrent.Future;

public interface Downloader {
	List<Future<Page>> downloads();
}
