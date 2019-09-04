package com.yilnz.surfing.core.tool;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.SurfPageProcessorInterface;
import com.yilnz.surfing.core.SurfSprider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PaginationTool implements Tool {
	private int step = 1;
	private Selector lastPageSelector;
	private String prefix;
	private int threadnum;
	private static final Logger logger = LoggerFactory.getLogger(PaginationTool.class);

	public PaginationTool(String prefix, Selector lastPageSelector,  int threadnum) {
		this.lastPageSelector = lastPageSelector;
		this.prefix = prefix;
		this.threadnum = threadnum;
	}

	public PaginationTool(String prefix, Selector lastPageSelector,  int threadnum, int step) {
		this.lastPageSelector = lastPageSelector;
		this.prefix = prefix;
		this.threadnum = threadnum;
		this.step = step;
	}

	@Override
	public void doWork(SurfPageProcessor pageProcessor, List<Future<Page>> pages){
		try {
			int lastPage = pages.get(0).get().getHtml().select(lastPageSelector).getInt();
			for(int i = step; i < lastPage; i+=step){
				SurfHttpRequest r = new SurfHttpRequest();
				r.setMethod("GET");
				r.setUrl(prefix + i);
				r.setData(i);
				SurfSprider.create().addRequest(r).thread(threadnum).setProcessor(pageProcessor).start();
			}
		} catch (InterruptedException | ExecutionException e) {
			logger.error("[surfing]Tool error", e);
		}

	}
}
