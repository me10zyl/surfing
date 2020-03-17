package com.yilnz.surfing.examples;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;

public class AndSelectorTest {
	public static void main(String[] args) {
		SurfHttpRequest request = new SurfHttpRequest();
		request.setUrl("http://www.baidu.com");
		request.setMethod("GET");
		final Page page = SurfSpider.create().addRequest(request).request().get(0);
		final Selectable select = page.getHtml().select(Selectors.$("a", true).and(Selectors.xpath("//*[@name=tj_trnews]")));
		System.out.println(select);
	}

}
