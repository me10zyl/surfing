package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSprider;

public class SpriderTest1 {
	public static void main(String[] args) {
		final SurfHttpRequest request = new SurfHttpRequest();
		request.setUrl("http://www.baidu.com");
		request.setMethod("GET");
		System.out.println(SurfSprider.create().addRequest(request).request());
	}
}
