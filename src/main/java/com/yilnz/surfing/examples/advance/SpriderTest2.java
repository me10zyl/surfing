package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.*;
import com.yilnz.surfing.core.basic.Page;

public class SpriderTest2 {
	public static void main(String[] args) {
		final SurfHttpRequest request = new SurfHttpRequest();
		request.setUrl("http://www.baidu.com");
		request.setMethod("GET");
		SurfSprider.create().addRequest(request).setProcessor(new SurfPageProcessor() {
			@Override
			public Site getSite() {
				return Site.me();
			}

			@Override
			public void process(Page page) {
				System.out.println(page.getHtml().get());
			}
		}).start();
	}
}
