package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;

public class TiebaTestWithThreads {
	/**
	 * 每次爬 5 页
	 * @param args 参数
	 */
	public static void main(String[] args) {
		final SurfHttpRequest request1 = new SurfHttpRequest();
		request1.setUrl("http://tieba.baidu.com/f?kw=java&fr=index");
		request1.setMethod("get");
		final Page request = SurfSpider.create().addRequest(request1).request().get(0);
		final Selectable lastpagea = request.getHtml().select(Selectors.$("a.pagination-item:last-of-type", "href"));
		final Selectable select = lastpagea.select(Selectors.regex("(?>pn=)(.+)", 1));
		for(int i = 50;i < select.getInt();i+=50){
			SurfHttpRequest r = new SurfHttpRequest();
			r.setMethod("get");
			r.setUrl(lastpagea.select(Selectors.replace("(.+)&pn=.+", "https:$1&pn=" + i)).get());
			int finalI = i;
			SurfSpider.create().addRequest(r).thread(5).setProcessor(new SurfPageProcessor() {
				@Override
				public void process(Page page) {
					page.getHtml().select(Selectors.$("a.j_th_tit")).nodes().forEach(e -> {
						System.out.println(e.get());
					});
				}}
			).start();
		}
	}
}
