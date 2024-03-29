package com.yilnz.surfing.examples.advance.tieba.test;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.exporter.Exporters;
import com.yilnz.surfing.core.plugin.HandlePage;
import com.yilnz.surfing.core.plugin.PaginationClz;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;
import com.yilnz.surfing.examples.advance.tieba.test.converter.TieziConverter;
import com.yilnz.surfing.examples.advance.tieba.test.entity.Tiezi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class TestTieba {
	private static final Logger logger = LoggerFactory.getLogger(TestTieba.class);

	public static void main(String[] args) {
		new TestTieba().testTieba();
	}

	public void testTieba() {
		SurfSpider.startPagination(new PaginationClz() {
			@Override
			public int getPageCount() {
				final SurfHttpRequest request1 = new SurfHttpRequest();
				request1.setUrl("http://tieba.baidu.com/f?kw=java&fr=index");
				request1.setMethod("get");
				final Page request = SurfSpider.create().addRequest(request1).request().get(0);
				final Selectable lastpagea = request.getHtml().select(Selectors.$("a.pagination-item:last-of-type", "href"));
				Selectable select = lastpagea.select(Selectors.regex("pn=(\\d+)", 1));
				return select.getInt() / 50;
			}

			@Override
			public SurfHttpRequest getPageUrl(int page) {
				return SurfHttpRequestBuilder.create("https://tieba.baidu.com/f?kw=java&amp;ie=utf-8&pn=" + page * 50, "GET").build();
			}

			@Override
			public HandlePage handlePage() {
				return new HandlePage() {
					@Override
					public void process(Page page, int currentPage) {
						logger.info("当前页：" + currentPage);
						List<Tiezi> tiezis = page.getHtml().toList(new TieziConverter(Selectors.$(".j_thread_list")));
						Exporters.CONSOLE.exportList(tiezis, "title", "author", "url");
					}

					@Override
					public void processError(Page page, int currentPage) {

					}
				};
			}

			@Override
			public SurfSpider surfSpider() {
				return SurfSpider.create();
			}
		});
	}
}
