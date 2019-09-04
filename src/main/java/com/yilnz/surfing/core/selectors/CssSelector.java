package com.yilnz.surfing.core.selectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CssSelector extends Selector {

    private String attr;
    private boolean containsTag;

    public CssSelector(String selectPattern) {
        super(selectPattern);
    }

    public CssSelector(String selectPattern, String attr) {
        super(selectPattern);
        this.attr = attr;
    }

    public CssSelector(String selectPattern, boolean containsTag) {
        super(selectPattern);
        this.containsTag = containsTag;
    }

    @Override
    public String select(String text) {
        Element element = new Element(text);
        final Element element1 = element.selectFirst(selectPattern);
        if(containsTag){
            return element1.toString();
        }
        if(this.attr != null) {
            return element1.attr(this.attr);
        }
        return element1.html();

    }

    @Override
    public List<String> selectList(String text) {
        final Document parse = Jsoup.parse(text);
        final Elements select = parse.select(selectPattern);
        final Iterator<Element> iterator = select.iterator();
        List<String> res = new ArrayList<>();
        while(iterator.hasNext()){
            final Element next = iterator.next();
            if(this.attr != null) {
                res.add(next.attr(this.attr));
            }else{
                String finalText = next.toString();
                if(!containsTag){
                    finalText = next.html();
                }
                res.add(finalText);
            }
        }
        return res;
    }
}
