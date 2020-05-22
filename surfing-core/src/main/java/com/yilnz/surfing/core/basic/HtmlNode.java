package com.yilnz.surfing.core.basic;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;

import java.util.List;

public class HtmlNode  extends PlainText{

    public HtmlNode(String text) {
        super(text);
    }

    public HtmlNode(List<String> text) {
        super(text);
    }

    public Element toElement(){
    	return Jsoup.parse(get());
	}
}
