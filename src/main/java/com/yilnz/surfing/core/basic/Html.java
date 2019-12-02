package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.selectors.AbstractSelectable;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;

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
