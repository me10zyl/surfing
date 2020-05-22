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
        if (selectList == null) {
           /* if(this instanceof Html){
                logger.warn("[surfing]选择结果为空 {} {} {}", ((Html)this).getUrl(), selector, this.text.get(0));
            }else{
                logger.warn("[surfing]选择结果为空 {} {}", selector, this.text.get(0));
            }*/

            return null;
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
        	return new HtmlNode(selectList);
		}
        return new PlainText(selectList);
    }

    public Json selectJson(String jsonPath){
		return (Json) this.select(Selectors.jsonPath(jsonPath));
	}

	public HtmlNode selectCss(String cssQuery){
    	return (HtmlNode) this.select(Selectors.$(cssQuery));
	}

	public HtmlNode selectXPath(String xpath){
    	return (HtmlNode) this.select(Selectors.xpath(xpath));
	}

    @Override
    public List<Selectable> nodes() {
        List<Selectable> plainTexts = new ArrayList<>();
        for (String text : this.text) {
            plainTexts.add(new PlainText(text));
        }
        return plainTexts;
    }

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
