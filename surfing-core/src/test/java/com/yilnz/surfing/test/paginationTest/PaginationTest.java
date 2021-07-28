package com.yilnz.surfing.test.paginationTest;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.exporter.Exporters;
import com.yilnz.surfing.core.plugin.HandlePage;
import com.yilnz.surfing.core.plugin.PaginationClz;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;
import com.yilnz.surfing.examples.advance.tieba.test.converter.TieziConverter;
import com.yilnz.surfing.examples.advance.tieba.test.entity.Tiezi;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PaginationTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        List<Future<Page>> futures = SurfSpider.startPagination(new PaginationClz() {
            @Override
            public int getPageCount() {
                return 5;
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
                        System.out.println("当前页：" + currentPage);
                        Html html = page.getHtml();
                        System.out.println(html);
                        List<Tiezi> tiezis = html.toList(new TieziConverter(Selectors.$(".j_thread_list")));
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
        //阻塞主线程
        for (Future<Page> future : futures) {
            future.get();
        }
    }
}
