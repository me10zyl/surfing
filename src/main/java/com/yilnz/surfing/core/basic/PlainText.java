package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.selectors.AbstractSelectable;
import com.yilnz.surfing.core.selectors.JsonSelector;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;

import java.util.ArrayList;
import java.util.List;

public class PlainText extends AbstractSelectable {

    protected List<String> text = new ArrayList<>();

    public PlainText(String text) {
        this.text.add(text);
    }

    public PlainText(List<String> text) {
        this.text = text;
    }

    @Override
    public String get() {
        if (this.text == null) {
            return null;
        }
        if (this.text.size() == 1) {
            return text.get(0);
        }
        if (this.text.size() == 0) {
            return null;
        }
        return text.toString();
    }

    @Override
    public Selectable select(Selector selector) {
        final List<String> selectList = selector.selectList(this.text.get(0));
        final List<Selector> otherSelectors = selector.getOtherSelectors();
        otherSelectors.forEach(otherSelector -> {
                    if ("AND".equals(otherSelector.getLogicType())) {
                        for (int i = selectList.size() - 1; i >=0 ; i--) {
                            final String select = selectList.get(i);
                            final List<String> otherSelectList = otherSelector.selectList(select);
                            selectList.remove(i);
                            selectList.addAll(i, otherSelectList);
                        }

                    } else if ("OR".equals(otherSelector.getLogicType())) {
                        selectList.addAll(otherSelector.selectList(this.text.get(0)));
                    }
                }
        );


        if(selector instanceof JsonSelector){
            return new Json(selectList);
        }
        return new PlainText(selectList);
    }


    @Override
    public List<Selectable> nodes() {
        List<Selectable> plainTexts = new ArrayList<>();
        for (String text : this.text) {
            plainTexts.add(new PlainText(text));
        }
        return plainTexts;
    }
}
