package com.yilnz.surfing.core.client;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.basic.Header;
import com.yilnz.surfing.core.basic.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class SurfRawClient {

    private Logger logger = LoggerFactory.getLogger(SurfRawClient.class);

    public Page post(SurfHttpRequest request){
        request.setMethod("POST");
        return request(request);
    }

    public Page get(SurfHttpRequest request){
        request.setMethod("GET");
        return request(request);
    }

    public Page request(SurfHttpRequest request){
        final Page page = new Page();
        try {
            URL url = new URL(request.getUrl());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(request.getMethod());
            final Map<String, String> requestHeaders = request.getHeaders();
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            if(request.getBody() != null && !request.getBody().equals("")){
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con
                        .getOutputStream());
                out.writeBytes(request.getBody());
                out.flush();
                out.close();
            }
            InputStream in;
            int statusCode = con.getResponseCode();
            if (statusCode >= 200 && statusCode < 300) {
                in = con.getInputStream();
            }else{
                in = con.getErrorStream();
            }

            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str = null;
            StringBuilder res = new StringBuilder();
            while ((str = br.readLine()) != null) {
                res.append(str);
            }
            final Map<String, List<String>> headerFields = con.getHeaderFields();
            final Map<String, String> headers = page.getHeaders();
            for (Map.Entry<String, List<String>> header : headerFields.entrySet()) {
                headers.put(header.getKey(), header.getValue().toString().replaceAll("^\\[|\\]$", ""));
            }
            StringBuilder headersText = new StringBuilder();
            for(Map.Entry<String, String> h : headers.entrySet()) {
                if (h.getKey() != null) {
                    headersText.append(h.getKey()).append(":").append(h.getValue()).append("\n");
                }
            }
            page.setRawText(headerFields.get(null).toString().replaceAll("^\\[|\\]$", "") + "\n"  + headersText + "\n" + res.toString());
        } catch (IOException e) {
            logger.error("[surfRawClient]ERROR", e);
            throw new RuntimeException(e.getMessage());
        }
        return page;
    }
}
