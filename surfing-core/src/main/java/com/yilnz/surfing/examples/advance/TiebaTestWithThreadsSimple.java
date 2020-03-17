package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.Site;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfPageProcessor;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectors;
import com.yilnz.surfing.core.tool.PaginationTool;

public class TiebaTestWithThreadsSimple {
	/**
     * 每次爬 5 页
	 * @param args
	 */
	public static void main(String[] args) {
		final SurfHttpRequest request1 = new SurfHttpRequest();
		request1.setUrl("http://tieba.baidu.com/f?kw=java&fr=index");
		request1.setMethod("get");
		request1.setData(1);
		SurfSpider.create(new PaginationTool("http://tieba.baidu.com/f?kw=java&fr=index&pn=",
				Selectors.$("a.pagination-item:last-of-type", "href")
						.and(Selectors.regex("(?>pn=)(.+)", 1)), 5, 50)).setSite(Site.me().setRetryTimes(2).setSleepTime(500)).setProcessor(new SurfPageProcessor() {

			@Override
			public void process(Page page) {
				final Html html = page.getHtml();
				html.select(Selectors.$("a.j_th_tit")).nodes().forEach(e->{
					System.out.println("第" + ((int)page.getData() / 50 > 0 ? (int)page.getData() / 50 : 1) + "页->" + e.get());
				});
			}

			@Override
			public void processError(Page page) {
				System.err.println("error page -> " +page.getData());
			}
		}).addRequest(request1).start();
	}
}
