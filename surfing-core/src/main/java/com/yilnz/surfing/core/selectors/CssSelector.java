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
    private Elements elements;

    public String getAttr() {
        return attr;
    }

    public boolean isContainsTag() {
        return containsTag;
    }

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
    public List<String> selectList(String text) {
        final Elements select = selectElements(text);
        final Iterator<Element> iterator = select.iterator();
        List<String> res = new ArrayList<>();
        while(iterator.hasNext()){
            final Element next = iterator.next();
            if(this.attr != null) {
                final String attr = next.attr(this.attr);
                if(!"".equals(attr)) {
                    res.add(attr);
                }
            }else{
                String finalText = next.toString();
                res.add(finalText);
            }
        }
        return res;
    }

    private Elements selectElements(String text) {
        final Document parse = Jsoup.parse(text);
        final Elements select = parse.select(selectPattern);
        if(select != null){
            if(this.elements == null){
                this.elements = select;
            }else {
                this.elements.addAll(select);
            }
        }
        return select;
    }

    public Elements getElements() {
        return elements;
    }
}
