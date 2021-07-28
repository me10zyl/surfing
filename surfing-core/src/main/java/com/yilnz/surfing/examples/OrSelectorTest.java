package com.yilnz.surfing.examples;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.stream.Collectors;

public class OrSelectorTest {
	public static void main(String[] args) {
		SurfHttpRequest request = new SurfHttpRequest();
		request.setUrl("http://www.baidu.com");
		request.setMethod("GET");
		final Page page = SurfSpider.create().addRequest(request).request().get(0);
		final Selectable select = page.getHtml().select(Selectors.$("a"));
		Selectable select1 = page.getHtml().select(Selectors.regex("<img.+?>"));
		List<? extends Selectable> nodes = select.nodes();
		List<? extends Selectable> nodes2 = select1.nodes();
		for (Selectable node : nodes) {
			System.out.println(node.get());
		}
		for (Selectable node : nodes2) {
			System.out.println(node.get());
		}
	}

}
