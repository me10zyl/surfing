package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Page;

public interface SurfPageProcessorInterface {

    void process(Page page);

	/**
	 * try times <= 0 (status code != 200)
	 * @param page
	 */
	void processError(Page page);
}
