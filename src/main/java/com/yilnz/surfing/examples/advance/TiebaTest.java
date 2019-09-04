package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.SurfHttpClient;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;

public class TiebaTest {
	public static void main(String[] args) {
		SurfHttpRequest request = new SurfHttpRequest();
		request.setUrl("http://tieba.baidu.com/f?kw=java&fr=index");
		final SurfHttpClient surfHttpClient = new SurfHttpClient();
		final Page page = surfHttpClient.get(request);
		final Html html = page.getHtml();
		final Selectable lastPageA = html.select(Selectors.$("a.pagination-item:last-of-type", "href"));
		final Selectable select = lastPageA.select(Selectors.regex("(?>pn=)(.+)", 1));
		for(int i = 50;i < select.getInt();i+=50){
			SurfHttpRequest r = new SurfHttpRequest();
			r.setUrl(lastPageA.select(Selectors.replace("(.+)&pn=.+", "https:$1&pn=" + i)).get());
			final Page page1 = surfHttpClient.get(r);
			page1.getHtml().select(Selectors.$("a.j_th_tit")).nodes().forEach(e->{
				System.out.println(e);
			});
		}

	}
}
