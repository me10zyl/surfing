package com.yilnz.surfing.core.selectors;

import com.yilnz.surfing.core.selectors.Selector;

import java.util.ArrayList;
import java.util.List;

public class ReplaceSelector extends Selector {
	private String replacement;
	public ReplaceSelector(String selectPattern, String replacement) {
		super(selectPattern);
		this.replacement = replacement;
	}

	@Override
	public String select(String text) {
		return this.selectList(text).get(0);
	}

	@Override
	public List<String> selectList(String text) {
		final String s = text.replaceAll(selectPattern, replacement);
		List<String> arr = new ArrayList<>();
		arr.add(s);
		return arr;
	}
}
