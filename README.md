# surfing 

易用的爬虫框架，支持 CSS、Regex、XPath、JSONPath 提取文本，也有同步、异步类型的请求，也支持多线程并发爬取网页。

# CSS Selector

用CSS选择器获取百度所有链接

``` java
  SurfHttpRequest request = new SurfHttpRequest();
         request.setUrl("http://www.baidu.com");
         request.setMethod("GET");
  final Page page = SurfSprider.create().addRequest(request).request();
  final Selectable select = page.getHtml().select(Selectors.$("a", true));
  final List<Selectable> nodes = select.nodes();
  nodes.forEach(e->{
      System.out.println(e);
  });
```

# Regex Selector

用正则表达式选择器和CSS选择器获取百度所有链接的name属性

```java
SurfHttpRequest request = new SurfHttpRequest();
request.setUrl("http://www.baidu.com");
final Page page = new SurfHttpClient().get(request);
final Selectable select = page.getHtml().select(Selectors.regex("<a.+?>.+?</a>"));
final List<Selectable> nodes = select.nodes();
nodes.forEach(e->{
    System.out.println(e.select(Selectors.$("a", "name")));
});
```
# 多线程并发爬取分页

一次爬5页百度贴吧，并打印帖子主题

```java
final SurfHttpRequest request1 = new SurfHttpRequest();
request1.setUrl("http://tieba.baidu.com/f?kw=java&fr=index");
request1.setMethod("get");
request1.setData(1);
SurfSprider.create(new PaginationTool("http://tieba.baidu.com/f?kw=java&fr=index&pn=",
        Selectors.$("a.pagination-item:last-of-type", "href")
                .and(Selectors.regex("(?>pn=)(.+)", 1)), 5, 50)).setProcessor(new SurfPageProcessor() {
    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(2).setSleepTime(500);
    }

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
```


# Thanks to
+ [https://github.com/code4craft/webmagic](https://github.com/code4craft/webmagic)