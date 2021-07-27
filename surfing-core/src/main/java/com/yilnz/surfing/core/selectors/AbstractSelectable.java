package com.yilnz.surfing.core.selectors;

import com.yilnz.surfing.core.basic.HtmlNode;
import com.yilnz.surfing.core.basic.Json;
import com.yilnz.surfing.core.filter.Filter;

public abstract class AbstractSelectable implements Selectable {

    @Override
    public String toString() {
        return this.get();
    }

    @Override
    public Integer getInt() {
        return Integer.parseInt(this.get());
    }

    @Override
    public Long getLong() {
        return Long.parseLong(this.get());
    }

    @Override
    public Boolean getBoolean() {
        return Boolean.parseBoolean(this.get());
    }

    @Override
    public Json selectJson(String jsonPath){
        return (Json) this.select(Selectors.jsonPath(jsonPath));
    }

    @Override
    public HtmlNode selectCss(String cssQuery){
        return (HtmlNode) this.select(Selectors.$(cssQuery));
    }

    @Override
    public HtmlNode selectXPath(String xpath){
        return (HtmlNode) this.select(Selectors.xpath(xpath));
    }


}
