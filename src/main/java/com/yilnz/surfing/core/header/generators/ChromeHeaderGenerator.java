package com.yilnz.surfing.core.header.generators;

import java.util.HashMap;
import java.util.Map;

public class ChromeHeaderGenerator implements HeaderGenerator {
    @Override
    public Map<? extends String, ? extends String> generateHeaders() {
        final HashMap<String, String> map = new HashMap<>();
        map.put("Connection", "keep-alive");
        map.put("Upgrade-Insecure-Requests", "1");
        map.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36");
        map.put("Sec-Fetch-User", "?1");
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        map.put("Sec-Fetch-Site", "none");
        map.put("Sec-Fetch-Mode", "navigate");
        //map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final Map<? extends String, ? extends String> map = generateHeaders();
        for (Map.Entry<? extends String, ? extends String> entry : map.entrySet()) {
            sb.append(entry.getKey() + ":" + entry.getValue() + "\n");
        }
        return sb.toString();
    }
}
