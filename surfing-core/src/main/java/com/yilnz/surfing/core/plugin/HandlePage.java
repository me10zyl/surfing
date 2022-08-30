package com.yilnz.surfing.core.plugin;

import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.SurfPageProcessorInterface;
import com.yilnz.surfing.core.basic.Page;

public interface HandlePage {


	void process(Page page, int currentPage);

	/**
	 * try times &lt;= 0 (status code != 200)
	 * @param page 页面
* @param currentPage 当前页
	 */
	void processError(Page page, int currentPage);
}
