package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.selectors.Selectable;

import java.util.ArrayList;
import java.util.List;

public class Json extends PlainText {

    public Json(String text) {
        super(text);
    }

    public Json(List<String> text) {
        super(text);
    }

    @Override
    public List<Selectable> nodes() {
        List<Selectable> plainTexts = new ArrayList<>();
        for (int i = 0; i < this.text.size(); i++) {
            plainTexts.add(new Json(this.text.get(i)));
        }
        return plainTexts;
    }
}
