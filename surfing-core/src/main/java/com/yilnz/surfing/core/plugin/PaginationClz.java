package com.yilnz.surfing.core.plugin;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.SurfSpider;

public interface PaginationClz {

	int getPageCount();

	SurfHttpRequest getPageUrl(int page);

	HandlePage handlePage();

	SurfSpider surfSpider();
}
