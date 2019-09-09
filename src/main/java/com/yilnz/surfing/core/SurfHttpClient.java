package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SurfHttpClient {

    private Logger logger = LoggerFactory.getLogger(SurfHttpClient.class);

    public Page post(SurfHttpRequest request){
        request.setMethod("POST");
        return request(request);
    }

    public Page get(SurfHttpRequest request){
        request.setMethod("GET");
        return request(request);
    }

    public Page request(SurfHttpRequest request){
        final HttpClientBuilder builder = HttpClientBuilder.create();
        final CloseableHttpClient closeableHttpClient = builder.build();

        Page page = new Page();
        page.setUrl(request.getUrl());
        try {
            final HttpEntityEnclosingRequestBase requests = new HttpEntityEnclosingRequestBase() {
                @Override
                public String getMethod() {
                    return request.getMethod().toUpperCase();
                }
            };

            String url = request.getUrl();
            if(!request.getParams().isEmpty()){
                url += "?" + getParamsStr(request.getParams());
            }
            requests.setURI(URI.create(url));
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                requests.setHeader(entry.getKey(), entry.getValue());
            }
            String body = request.getBody();
            if(body == null && !request.getBodyParams().isEmpty()){
                body = getParamsStr(request.getBodyParams());
                requests.setEntity(new StringEntity(body));
            }
            requestInternal(page,closeableHttpClient, requests);
        } catch (IOException e) {
            logger.error("surf http client error", e);
        }
        return page;

    }

    private String getParamsStr(final Map<String, String> params) throws IOException {
        final List<NameValuePair> formparams = new ArrayList<>();
        params.forEach((e,v)->{
            formparams.add(new BasicNameValuePair(e, v));
        });
        return EntityUtils.toString(new UrlEncodedFormEntity(formparams, "UTF-8"));
    }

    private void requestInternal(Page page, CloseableHttpClient closeableHttpClient, HttpUriRequest requests) throws IOException {
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
