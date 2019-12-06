package com.yilnz.surfing.core.proxy.ippool;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfSprider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.exception.NoProxyException;
import com.yilnz.surfing.core.header.generators.BulkHeaderGenerator;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class IPPool {
    private static final Logger logger = LoggerFactory.getLogger(IPPool.class);

    public static HttpProxy randomProxy() {
        final List<HttpProxy> proxyList = getIP66();
        proxyList.addAll(getXici());
        return proxyList.get(new Random().nextInt(proxyList.size()));
    }

    public static List<HttpProxy> getIP66_2() {
        return extractProxyListFromURL("http://www.66ip.cn/index.html", "#main");
    }

    public static List<HttpProxy> getIP66() {
        try {
            final SurfHttpRequest request = new SurfHttpRequest("http://www.66ip.cn/index.html");
            request.setConnectTimeout(3000);
            final Html html = SurfSprider.get(request).getHtml();
            final Integer pageSize = html.select(Selectors.$("#PageList a:nth-last-of-type(2)")).getInt();
            final Random random = new Random();
            int ranPage = random.nextInt(pageSize) + 1;
            return extractProxyListFromURL("http://www.66ip.cn/" + ranPage + ".html", "#main");
        } catch (Exception e) {
            logger.error("[surfing]get ip66 error", e);
        }
        return new ArrayList<>();
    }

    public static List<HttpProxy> getXici() {
        try {
            return extractProxyListFromURL("https://www.xicidaili.com/", "#ip_list", 1, 2);
        } catch (Exception e) {
            logger.error("[surfing]get xici error", e);
        }
        return new ArrayList<>();
    }

    public static List<HttpProxy> extractProxyListFromURL(String url, String tableCssSelector) {
        return extractProxyListFromURL(url, tableCssSelector, 0, 1);
    }

    public static List<HttpProxy> extractProxyListFromURL(String url, String tableCssSelector, int ipIndex, int portIndex) {
        final SurfHttpRequest request = new SurfHttpRequest();
        request.setMethod("GET");
        request.setUrl(url);
        request.setConnectTimeout(3000);
        request.setHeaderGenerator(new BulkHeaderGenerator("Connection: keep-alive\n" +
                "Cache-Control: max-age=0\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36\n" +
                "Sec-Fetch-User: ?1\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\n" +
                "Sec-Fetch-Site: none\n" +
                "Sec-Fetch-Mode: navigate\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\n" +
                "Cookie: _free_proxy_session=BAh7B0kiD3Nlc3Npb25faWQGOgZFVEkiJTNkNjg3NjA0Y2Y3ZmRlM2RlYzQyMGI0M2I5ZTQ2N2E5BjsAVEkiEF9jc3JmX3Rva2VuBjsARkkiMStiWFVMcERRbDN4UDZqV1RTZ0dmQjBuMU5ROU5MT1FqWEFvWXNrZSt2Wk09BjsARg%3D%3D--b2b99afbab5a6ac508e7b1f2dc8c6f11071aa631; Hm_lvt_0cf76c77469e965d2957f0553e6ecf59=1575275614,1575275734,1575275738,1575275739; Hm_lpvt_0cf76c77469e965d2957f0553e6ecf59=1575344636\n" +
                "If-None-Match: W/\"d1ccb5e3391d5b02cc58a5e389426f27\""));
        return extractProxyListFromURL(request, null, tableCssSelector, ipIndex, portIndex);
    }

    public static List<HttpProxy> extractProxyListFromURL(SurfHttpRequest request, ProxyProvider proxyProvider, String tableCssSelector, int ipIndex, int portIndex) {
        final SurfSprider surfSprider = SurfSprider.create();
        if (proxyProvider != null) {
            surfSprider.setProxyProvider(proxyProvider);
        }
        final Page page = surfSprider.addRequest(request).request();
        final List<Selectable> nodes = page.getHtml().select(Selectors.$(tableCssSelector + " tr:nth-of-type(n+2)")).nodes();
        List<HttpProxy> httpHosts = new ArrayList<>();
        for (Selectable node : nodes) {
            final Selectable td = node.select(Selectors.regex("<td.*?>(.*?)</td>", 1));
            if (td != null) {
                if (portIndex == -1) {
                    httpHosts.add(new HttpProxy(td.nodes().get(ipIndex).get()));
                }else {
                    httpHosts.add(new HttpProxy(td.nodes().get(ipIndex).get(), Integer.parseInt(td.nodes().get(portIndex).get())));
                }
            }
        }
        return httpHosts;
    }

    public static List<HttpProxy> getProxyList() {
        return getProxyList(false);
    }

    public static List<HttpProxy> getProxyList(boolean noCacheAndOverwrite) {
        if(!noCacheAndOverwrite) {
            final List<HttpProxy> storedProxyList = getCachedProxyListWithLog();
            if (storedProxyList != null) return storedProxyList;
        }
        final List<HttpProxy> ip66 = getIP66();
        if (ip66.size() > 0) {
            logger.info("[surfing]获取了IP66代理");
            saveCachedProxyList(ip66, null, null);
            return ip66;
        }
        final List<HttpProxy> xici = getXici();
        if (xici.size() > 0) {
            logger.info("[surfing]获取了西刺代理");
            saveCachedProxyList(xici, null, null);
            return xici;
        }
        throw new NoProxyException("[surfing]没用从网上找到任何可用的代理");
    }

    public static List<HttpProxy> getCachedProxyListWithLog() {
        final List<HttpProxy> storedProxyList = getCachedProxyList();
        if (storedProxyList.size() > 0) {
            final URL resource = Thread.currentThread().getContextClassLoader().getResource("proxy.properties");
            assert resource != null;
            logger.info("[surfing]使用代理列表缓存：{}" , resource.getPath());
            return storedProxyList;
        }
        return null;
    }

    private static String values(List<HttpProxy> httpProxies){
        return httpProxies.toString().replaceAll("\\[|\\]", "");
    }

    public static void saveCachedProxyList(List<HttpProxy> usedProxyList, List<HttpProxy> reservedProxyList, List<HttpProxy> removedProxyList){
        Properties properties = new Properties();
        properties.setProperty("proxy", values(usedProxyList));
        if (removedProxyList != null) {
            properties.setProperty("proxy_reserved", values(reservedProxyList));
        }
        if (reservedProxyList != null) {
            properties.setProperty("proxy_removed", values(removedProxyList));
        }
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("");
        final File file = new File(resource.getPath(), "proxy.properties");
        try {
            properties.store(new FileOutputStream(file), "proxy list");
            logger.info("[surfing]存储代理列表缓存：{}" , file.getPath());
        } catch (IOException e) {
            logger.error("[surfing]properties store error", e);
        }
    }

    public static List<HttpProxy> getCachedProxyList() {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("proxy.properties");
        if (resource == null) {
            return new ArrayList<>();
        }
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
}
