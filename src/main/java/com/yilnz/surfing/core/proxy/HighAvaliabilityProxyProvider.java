package com.yilnz.surfing.core.proxy;

import com.yilnz.surfing.core.basic.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HighAvaliabilityProxyProvider extends LoadBalancingProxyProvider {

    private static final Logger logger = LoggerFactory.getLogger(HighAvaliabilityProxyProvider.class);

    protected Map<HttpProxy, Integer> proxyErrorCount = new HashMap<>();
    protected Set<HttpProxy> removedProxyList = new HashSet<>();
    protected Set<HttpProxy> reservedProxyList = new HashSet<>();
    protected Set<HttpProxy> usedProxyList = new HashSet<>();


    public HighAvaliabilityProxyProvider(List<HttpProxy> proxyList) {
        super(proxyList);
        if (this.getProxyList() != null) {
            reservedProxyList.addAll(this.getProxyList());
        }
        /*String basePathOfClass = getClass()
                .getProtectionDomain().getCodeSource().getLocation().getFile();*/
       /* final URL resource = Thread.currentThread().getContextClassLoader().getResource("proxies_generated.properties");
        file =new File(resource.getPath());
        try {
            properties.put("all_proxy", reservedProxyList.toString());
            properties.store(new FileOutputStream(file), "proxy list");
        } catch (IOException e) {
            logger.warn("[surfing]properties error", e);
        }*/
    }

    protected void onRemoveProxy(HttpProxy removed) {

    }

    @Override
    public void pageReturn(HttpProxy proxy, Page page) {
        if(page.isConnectionError()){
            if (!removedProxyList.contains(proxy) && proxyErrorCount.containsKey(proxy)) {
                proxyErrorCount.put(proxy, proxyErrorCount.get(proxy) + 1);
            }
        }else{
            proxyErrorCount.put(proxy, 0);
            usedProxyList.add(proxy);
        }
        proxyErrorCount.putIfAbsent(proxy, 0);
        //失败次数5次
        if(proxyErrorCount.get(proxy) >= 5){
            final List<HttpProxy> proxyList = this.getProxyList();
            if(proxyList.size() > 1) {
                logger.warn("[surfing]移除不可用的代理：{}, 可用的代理剩余：{}", proxy, proxyList);
                proxyList.remove(proxy);
                removedProxyList.add(proxy);
                this.sequence = 0;
                onRemoveProxy(proxy);
            } else{
                logger.error("[surfing]最后一个代理未移除，因为没有任何可用的代理了");
            }
        }
        /*try {
            properties.put("removed_proxy", removedProxyList.toString());
            properties.put("left_proxy", this.getProxyList().toString());
            properties.put("used_proxy", usedProxyList.toString());
            properties.store(new FileOutputStream(file), "proxy list");
        } catch (IOException e) {
            logger.warn("[surfing]properties error", e);
        }*/
    }
}
