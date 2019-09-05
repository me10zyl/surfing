package com.yilnz.surfing.examples;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSprider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;

public class OrSelectorTest {
	public static void main(String[] args) {
		SurfHttpRequest request = new SurfHttpRequest();
		request.setUrl("http://www.baidu.com");
		request.setMethod("GET");
		final Page page = SurfSprider.create().addRequest(request).request();
		final Selectable select = page.getHtml().select(Selectors.$("a", true).or(Selectors.regex("<img.+?>")));
		select.nodes().forEach(e->{
			System.out.println(e);
		});
	}

}
