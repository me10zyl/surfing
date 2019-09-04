# surfing 

易用的爬虫框架

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

# Thanks to
+ [https://github.com/code4craft/webmagic](https://github.com/code4craft/webmagic)