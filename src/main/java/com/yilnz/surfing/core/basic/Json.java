package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.selectors.AbstractSelectable;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;

import java.util.List;

public class Json extends PlainText {

    public Json(String text) {
        super(text);
    }

    public Json(List<String> text) {
        super(text);
    }
}
