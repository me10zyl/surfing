package com.yilnz.surfing.core.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import net.minidev.json.annotate.JsonIgnore;
import org.apache.http.HttpHost;

import java.util.Objects;

public class HttpProxy{
   private String host;
   private int port;
   private String schema;
   private boolean isOverGFW;

   public static HttpProxy RANDOM_PROXY = new HttpProxy("-1", -1);

   public HttpProxy(String proxyString){
       proxyString = proxyString.trim();
       final int i = proxyString.indexOf("://");
       if (i != -1) {
           this.schema = proxyString.substring(0, i);
           proxyString = proxyString.substring(i + 3);
       }
       this.host = proxyString.split(":")[0];
       this.port = Integer.parseInt(proxyString.split(":")[1]);
       if (schema == null) {
           this.schema = "http";
       }
   }

    public HttpProxy(String host, int port, String schema) {
        this.host = host;
        this.port = port;
        this.schema = schema;
        if (schema == null) {
            this.schema = "http";
        }
    }

    public boolean isOverGFW() {
        return isOverGFW;
    }

    public void setOverGFW(boolean overGFW) {
        isOverGFW = overGFW;
    }

    public HttpProxy(String host, int port) {
        this(host, port, null);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getSchema() {
        return schema == null ? null : schema.toLowerCase();
    }

    public HttpHost _getHttpHost(){
        if (host.equals("-1") && port == -1) {
            return null;
        }
        return new HttpHost(host, port, schema);
    }

    public static HttpProxy fromJSON(String str){
        return JSON.parseObject(str, HttpProxy.class);
    }

    public String toJSON(){
        return JSON.toJSONString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpProxy httpProxy = (HttpProxy) o;
        return port == httpProxy.port &&
                host.equals(httpProxy.host) &&
                Objects.equals(schema, httpProxy.schema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, schema);
    }

    @Override
    public String toString() {
        return getSchema() + "://" + host + ":" + port;
    }
}
