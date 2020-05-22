package com.yilnz.surfing.core.converter;

import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;

import java.util.ArrayList;
import java.util.List;

public abstract class Converter<T>  {

	private Selector rowSelector;

	public abstract T convert(Selectable selectable);

	public Converter(Selector rowSelector) {
		this.rowSelector = rowSelector;
	}

	public List<T> toList(Selectable selectable){
		Selectable row = selectable.select(rowSelector);
		List<T> list = new ArrayList<>();
		for (Selectable node : row.nodes()) {
			T convert = convert(node);
			if(convert != null){
				list.add(convert);
			}
		}
		return list;
	}
}
