package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectors;

public class AndOrTest {
	public static void main(String[] args) {
		final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
		surfHttpRequest.setMethod("get");
		surfHttpRequest.setUrl("http://www.baidu.com");
		final Page page = SurfSpider.create().addRequest(surfHttpRequest).request().get(0);
		page.getHtml().select(Selectors.$("a", "href")
				.or(Selectors.$("img", "src"))
				.and(Selectors.regex("^//.+"))).nodes().forEach(e->{
			System.out.println(e.get());
		});
	}
}
