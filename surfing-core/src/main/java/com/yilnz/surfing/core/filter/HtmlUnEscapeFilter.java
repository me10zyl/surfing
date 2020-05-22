package com.yilnz.surfing.core.filter;

import org.apache.commons.text.StringEscapeUtils;

public class HtmlUnEscapeFilter extends Filter {
	@Override
	public Object doFilter(Object originData) {
		return StringEscapeUtils.unescapeHtml4((String) originData);
	}
}
