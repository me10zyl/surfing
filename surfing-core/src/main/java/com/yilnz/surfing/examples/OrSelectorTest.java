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
		final Selectable select = page.getHtml().select(Selectors.$("a", true));
		Selectable select1 = page.getHtml().select(Selectors.regex("<img.+?>"));
		List<Selectable> nodes = select.nodes();
		nodes.addAll(select1.nodes());
		for (Selectable node : nodes) {
			System.out.println(node.get());
		}
//		String collect = select.nodes().stream().map(e -> e.get()).collect(Collectors.joining("\n"));
		/*Document parse = Jsoup.parse(collect);
		System.out.println(parse.select("a:nth-last-child(2)"));*/
		/*select.nodes().forEach(e->{
			System.out.println(e);
		});*/
	}

}
