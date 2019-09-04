package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.selectors.AbstractSelectable;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;

import java.util.ArrayList;
import java.util.List;

public class PlainText extends AbstractSelectable {

    private List<String> text = new ArrayList<>();

    public PlainText(String text) {
        this.text.add(text);
    }

    public PlainText(List<String> text) {
        this.text = text;
    }

    @Override
    public String get() {
        if(this.text == null){
            return null;
        }
        if(this.text.size() == 1){
            return text.get(0);
        }
        if(this.text.size() == 0){
            return null;
        }
        return text.toString();
    }

    @Override
    public Selectable select(Selector selector) {
        final List<String> strings = selector.selectList(this.text.get(0));
        final List<Selector> otherSelectors = selector.getOtherSelectors();
        List<String> lastString = new ArrayList<>();
        for (String string : strings) {
            List<String> finalLastString = lastString;
            otherSelectors.forEach(e->{
                if("AND".equals(e.getLogicType())){
                    finalLastString.addAll(e.selectList(string));
                } else if ("OR".equals(e.getLogicType())) {
                    finalLastString.add(string);
                    finalLastString.addAll(e.selectList(string));
                }
            });
        }

        if(otherSelectors.size() == 0){
            lastString = strings;
        }
        return new PlainText(lastString);
    }

    @Override
    public List<Selectable> nodes() {
        List<Selectable> plainTexts = new ArrayList<>();
        for(String text : this.text){
            plainTexts.add(new PlainText(text));
        }
        return plainTexts;
    }
}
