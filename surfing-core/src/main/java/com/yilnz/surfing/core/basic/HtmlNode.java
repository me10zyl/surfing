package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.filter.Filter;
import com.yilnz.surfing.core.selectors.AbstractSelectable;
import com.yilnz.surfing.core.selectors.CssSelector;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;
import org.jsoup.Jsoup;

import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;

public class HtmlNode  extends PlainText {

    public HtmlNode(String text) {
        super(text);
    }

    private CssSelector cssSelector;

    HtmlNode(List<String> text, CssSelector cssSelector){
        super(text);
        this.cssSelector = cssSelector;
    }

    public HtmlNode(List<String> text) {
        super(text);
    }

    @Override
    public String get() {
        if (this.text.size() == 0) {
            return null;
        }
        if(cssSelector != null && cssSelector.isContainsTag()){
            return this.text.get(0);
        }
        return Jsoup.parse(this.text.get(0)).text();
    }

    @Override
    public Selectable select(Selector selector) {
        return super.select(selector);
    }

    @Override
    public List<HtmlNode> nodes() {
        List<HtmlNode> plainTexts = new ArrayList<>();
        for (String text : this.text) {
            plainTexts.add(new HtmlNode(text));
        }
        return plainTexts;
    }


    public String wrapTag(){
        if (this.text.size() == 0) {
            return null;
        }
        return this.text.get(0);
    }

    public String attr(String attrName){
        return Jsoup.parse(this.text.get(0)).attr(attrName);
    }
}
