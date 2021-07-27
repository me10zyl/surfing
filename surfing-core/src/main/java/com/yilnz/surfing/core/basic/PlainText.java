package com.yilnz.surfing.core.basic;

import com.yilnz.surfing.core.converter.Converter;
import com.yilnz.surfing.core.selectors.*;
import com.yilnz.surfing.core.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PlainText extends AbstractSelectable {

    private static final Logger logger = LoggerFactory.getLogger(PlainText.class);

    protected List<String> text = new ArrayList<>();

    public PlainText(String text) {
        this.text.add(text);
    }

    public PlainText(List<String> text) {
        this.text = text;
    }

    @Override
    public String get() {
        if (this.text.size() == 0) {
            return null;
        }
        return text.get(0);
    }

    @Override
    public Selectable select(Selector selector) {
        //page.getHtml().selectJson("g").selectJson("f").get() situation
        if(this.text.size() == 0){
            if(selector instanceof JsonSelector){
                return new Json(new ArrayList<>());
            }
            if(selector instanceof CssSelector){
                return new HtmlNode(new ArrayList<>());
            }
            return new PlainText(new ArrayList<>());
        }

        final List<String> selectList = selector.selectList(this.text.get(0));
        if (selectList == null) {
            if(selector instanceof JsonSelector){
                return new Json(new ArrayList<>());
            }
            if(selector instanceof CssSelector){
                return new HtmlNode(new ArrayList<>());
            }
            return new PlainText(new ArrayList<>());
        }

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
        if(selector instanceof CssSelector){
        	return new HtmlNode(selectList, (CssSelector) selector);
		}
        return new PlainText(selectList);
    }

    @Override
    public List<? extends Selectable> nodes() {
        List<Selectable> plainTexts = new ArrayList<>();
        for (String text : this.text) {
            plainTexts.add(new PlainText(text));
        }
        return plainTexts;
    }

    @Override
    public Selectable filter(Filter... filters){

		for (Filter filter : filters) {
			for (int i =0 ;i< this.nodes().size();i++) {
				Object o = filter.doFilter(this.nodes().get(i).get());
				this.text.set(i, (String)o);
			}
		}

    	return this;
	}

	public <T> List<T> toList(Converter<T> converter){
		return converter.toList(this);
	}
}
