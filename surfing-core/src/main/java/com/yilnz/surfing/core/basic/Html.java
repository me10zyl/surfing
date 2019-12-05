package com.yilnz.surfing.core.basic;

import java.util.List;

public class Html extends HtmlNode{

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Html(String text) {
        super(text);
    }

    public Html(List<String> text) {
        super(text);
    }
}
