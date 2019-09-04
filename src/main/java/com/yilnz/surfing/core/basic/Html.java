package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.selectors.AbstractSelectable;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;

import java.util.List;

public class Html extends HtmlNode{

    public Html(String text) {
        super(text);
    }

    public Html(List<String> text) {
        super(text);
    }
}
