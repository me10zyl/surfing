package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSpider;

public class SpriderTest1 {
	public static void main(String[] args) {
		final SurfHttpRequest request = new SurfHttpRequest();
		request.setUrl("http://www.baidu.com");
		request.setMethod("GET");
		System.out.println(SurfSpider.create().addRequest(request).request());
	}
}
