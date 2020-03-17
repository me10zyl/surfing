package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.SurfSpider;

public class SpriderTest2 {
	public static void main(String[] args) {
		final SurfHttpRequest request = new SurfHttpRequest();
		request.setUrl("http://www.baidu.com");
		request.setMethod("GET");
		SurfSpider.create().addRequest(request).setProcessor(new SurfPageProcessor() {
			@Override
			public void process(Page page) {
				System.out.println(page.getHtml().get());
			}
		}).start();
	}
}
