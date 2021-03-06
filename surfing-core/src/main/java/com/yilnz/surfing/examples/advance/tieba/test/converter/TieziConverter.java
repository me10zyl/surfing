package com.yilnz.surfing.examples.advance.tieba.test.converter;

import com.yilnz.surfing.core.converter.Converter;
import com.yilnz.surfing.core.filter.Filters;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;
import com.yilnz.surfing.core.selectors.Selectors;
import com.yilnz.surfing.examples.advance.tieba.test.entity.Tiezi;

public class TieziConverter extends Converter<Tiezi> {

	public TieziConverter(Selector rowSelector) {
		super(rowSelector);
	}

	@Override
	public Tiezi convert(Selectable selectable) {
		Tiezi tiezi = new Tiezi();
		//TM.toTmpFile(selectable.filter(Filters.HTML_UNESCAPE).get());
		tiezi.setTitle(selectable.selectCss("a.j_th_tit").filter(Filters.TRIM).get());
		tiezi.setAuthor(selectable.select(Selectors.$(".tb_icon_author", "title")).filter(Filters.TRIM).get());
		tiezi.setUrl("https://tieba.baidu.com" + selectable.select(Selectors.$(".j_th_tit", "href")).get());
		return tiezi;
	}

}
