package com.yilnz.surfing.core.tool;

import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.basic.Page;

import java.util.List;
import java.util.concurrent.Future;

public interface Tool {
	void doWork(SurfPageProcessor pageProcessor, List<Future<Page>> pages);
}
