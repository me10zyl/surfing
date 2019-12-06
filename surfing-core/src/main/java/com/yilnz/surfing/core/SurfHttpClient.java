package com.yilnz.surfing.core;

import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.core.proxy.ippool.IPPool;
import com.yilnz.surfing.core.proxy.ProxyProvider;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.brotli.dec.BrotliInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class BrotliDecompressingEntity extends DecompressingEntity {
    BrotliDecompressingEntity(HttpEntity entity) {
        super(entity, new InputStreamFactory() {
            public InputStream create(InputStream instream) throws IOException {
                return new BrotliInputStream(instream);
            }
        });
    }
}

public class SurfHttpClient {

    private Logger logger = LoggerFactory.getLogger(SurfHttpClient.class);

    private HttpProxy proxy;

    public HttpProxy getProxy() {
        return proxy;
    }

    public void setProxy(HttpProxy proxy) {
        this.proxy = proxy;
    }


    private ProxyProvider proxyProvider;


    public ProxyProvider getProxyProvider() {
        return proxyProvider;
    }

    public void setProxyProvider(ProxyProvider proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    public Page post(SurfHttpRequest request){
        request.setMethod("POST");
        return request(request);
    }

    public Page get(SurfHttpRequest request){
        request.setMethod("GET");
        return request(request);
    }

    public Page request(SurfHttpRequest request){
        final RequestConfig.Builder custom = RequestConfig.custom();
        if (request.isIgnoreCookie()) {
            custom.setCookieSpec(CookieSpecs.IGNORE_COOKIES);
        }
        if (request.getConnectTimeout() != null) {
            custom.setSocketTimeout(Math.toIntExact(request.getConnectTimeout()));
            custom.setConnectTimeout(Math.toIntExact(request.getConnectTimeout()));
            custom.setConnectionRequestTimeout(Math.toIntExact(request.getConnectTimeout()));
        }
        RequestConfig globalConfig = custom.build();
        //final HttpClientBuilder builder = HttpClientBuilder.create();
        final HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(globalConfig);
        boolean useProxy = false;
        if (proxy != null) {
            if(proxy == HttpProxy.RANDOM_PROXY){
                proxy = IPPool.randomProxy();
            }
            httpClientBuilder.setProxy(proxy._getHttpHost());
            useProxy = true;
        }
        if (proxyProvider != null) {
            if(proxyProvider.getProxy() == HttpProxy.RANDOM_PROXY){
                proxy = IPPool.randomProxy();
            }else{
                proxy = proxyProvider.getProxy();
            }
            httpClientBuilder.setProxy(proxy._getHttpHost());
            useProxy = true;
        }
        if(useProxy){
            logger.info("[surfing]使用代理:" + proxy._getHttpHost());
        }
        final CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        Page page = new Page();
        page.setUrl(request.getUrl());
        page.setHtml(new Html(""));
        page._toUseProx(proxy);
        if(request.getMethod() == null){
            request.setMethod("GET");
        }
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
            }else if(body != null){
                requests.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
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
        final Header encoding = response.getFirstHeader("content-encoding");
        HttpEntity entity;
        if(encoding != null && encoding.getValue().equals("gzip")){
            entity = new GzipDecompressingEntity(response.getEntity());
        } else if(encoding != null && encoding.getValue().equals("deflate")){
            entity = new DeflateDecompressingEntity(response.getEntity());
        } else if(encoding != null && encoding.getValue().equals("br")){
            entity = new BrotliDecompressingEntity(response.getEntity());
        } else{
            entity = response.getEntity();
            //logger.info("[surfing]ContentEncoding:"+ encoding);

        }
      /*  final InputStream inputStream = entity.getContent();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str;
        StringBuilder result = new StringBuilder();
        while((str = bufferedReader.readLine()) != null){
            result.append(str);
        }*/
        final int statusCode = response.getStatusLine().getStatusCode();
        page.setStatusCode(statusCode);
        page.setHtml(new Html(EntityUtils.toString(entity)));
        final Header[] allHeaders = response.getAllHeaders();
        for (Header h : allHeaders) {
            page.getHeaders().add(new com.yilnz.surfing.core.basic.Header(h.getName(), h.getValue()));
        }
    }
}
