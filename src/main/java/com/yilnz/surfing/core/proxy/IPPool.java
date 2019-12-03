package com.yilnz.surfing.core.proxy;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSprider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.header.generators.BulkHeaderGenerator;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class IPPool {
    private static final Logger logger = LoggerFactory.getLogger(IPPool.class);

    public static HttpProxy randomProxy(){
        final List<HttpProxy> proxyList = getProxyList();
        return proxyList.get(new Random().nextInt(proxyList.size()));
    }

    public static List<HttpProxy> getProxyList(){
        final Html html = SurfSprider.get("http://www.66ip.cn/index.html").getHtml();
        final Integer pageSize = html.select(Selectors.$("#PageList a:nth-last-of-type(2)")).getInt();
        final Random random = new Random();
        final int ranPage = random.nextInt(pageSize + 1);
        final Page page = SurfSprider.get("http://www.66ip.cn/" + ranPage + ".html");
        final List<Selectable> nodes = page.getHtml().select(Selectors.$("#main tr:nth-of-type(n+2)")).nodes();
        List<HttpProxy> httpHosts = new ArrayList<>();
        for (Selectable node : nodes) {
            final Selectable td = node.select(Selectors.regex("<td>(.+)</td>", 1));
            httpHosts.add(new HttpProxy(td.nodes().get(0).get(), Integer.parseInt(td.nodes().get(1).get())));
        }
        return httpHosts;
    }

    public static List<HttpProxy> getStoredProxyList(){
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("proxy.properties");
        final ArrayList<HttpProxy> httpProxies = new ArrayList<>();
        try {
            final FileInputStream resourceAsStream = new FileInputStream(resource.getPath());
        if (resourceAsStream == null) {
            return httpProxies;
        }
        Properties properties = new Properties();

            properties.load(resourceAsStream);
            final String proxy = properties.getProperty("proxy");
            final String[] split = proxy.split(",");
            httpProxies.addAll(Arrays.stream(split).map(HttpProxy::new).collect(Collectors.toList()));
        } catch (IOException e) {
            logger.error("[surfing]Properties load error", e);
        }
        return httpProxies;
    }

    public static void main(String[] args) {
        System.out.println(IPPool.getProxyList());
    }
}
