package com.yilnz.surfing.test.converter;

import com.yilnz.surfing.core.converter.Converter;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;
import com.yilnz.surfing.core.selectors.Selectors;
import com.yilnz.surfing.core.filter.Filters;
import com.yilnz.surfing.test.entity.Tiezi;
import com.yilnz.surfing.testmanager.TM;

public class TieziConverter extends Converter<Tiezi> {

	public TieziConverter(Selector rowSelector) {
		super(rowSelector);
	}

	@Override
	public Tiezi convert(Selectable selectable) {
		Tiezi tiezi = new Tiezi();
		//TM.toTmpFile(selectable);
		tiezi.setTitle(selectable.selectCss("a.j_th_tit").filter(Filters.TRIM).get());
		tiezi.setAuthor(selectable.select(Selectors.$(".tb_icon_author", "title")).filter(Filters.TRIM).get());
		tiezi.setUrl(selectable.selectCss(".j_th_tit").get());
		return tiezi;
	}

}
