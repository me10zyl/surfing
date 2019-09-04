package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SurfHttpClient {

    private Logger logger = LoggerFactory.getLogger(SurfHttpClient.class);

    public Page post(SurfHttpRequest request){
        final HttpClientBuilder builder = HttpClientBuilder.create();
        final CloseableHttpClient closeableHttpClient = builder.build();
        Page page = new Page();
        try {
            final HttpPost requests = new HttpPost(request.getUrl());
            String body = request.getBody();
            if(body == null){
                body = getParamsStr(request);
            }
            requests.setEntity(new StringEntity(body));
            request(page,closeableHttpClient, requests);
        } catch (IOException e) {
           logger.error("surf http client post error", e);
        }
        return page;
    }

    public Page get(SurfHttpRequest request){
        final HttpClientBuilder builder = HttpClientBuilder.create();
        final CloseableHttpClient closeableHttpClient = builder.build();
        Page page = new Page();
        try {
            String paramsStr = getParamsStr(request);
            final HttpGet requests = new HttpGet(request.getUrl() + "?" + paramsStr);
            request(page, closeableHttpClient, requests);
        } catch (IOException e) {
            logger.error("surf http client get error", e);
        }
        return page;
    }

    private String getParamsStr(final SurfHttpRequest request) throws IOException {
        final List<NameValuePair> formparams = new ArrayList<>();
        request.getParams().forEach((e,v)->{
            formparams.add(new BasicNameValuePair(e, v));
        });
        return EntityUtils.toString(new UrlEncodedFormEntity(formparams, "UTF-8"));
    }

    private void request(Page page, CloseableHttpClient closeableHttpClient, HttpUriRequest requests) throws IOException {
        final CloseableHttpResponse response = closeableHttpClient.execute(requests);
        final HttpEntity entity = response.getEntity();
        final InputStream inputStream = entity.getContent();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str;
        StringBuilder result = new StringBuilder();
        while((str = bufferedReader.readLine()) != null){
            result.append(str);
        }
        final int statusCode = response.getStatusLine().getStatusCode();
        page.setStatusCode(statusCode);
        page.setHtml(new Html(result.toString()));
    }
}
