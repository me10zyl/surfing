package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.filter.Filter;
import com.yilnz.surfing.core.selectors.AbstractSelectable;
import com.yilnz.surfing.core.selectors.CssSelector;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class HtmlNode  extends PlainText {

    public HtmlNode(String text) {
        super(text);
    }


    HtmlNode(List<String> text){
        super(text);
    }

    @Override
    public String get() {
        if (this.text.size() == 0) {
            return null;
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

    public String outerHtml(){
        if (this.text.size() == 0) {
            return null;
        }
        return this.text.get(0);
    }
}
