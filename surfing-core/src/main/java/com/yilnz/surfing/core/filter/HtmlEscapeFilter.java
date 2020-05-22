package com.yilnz.surfing.core.filter;

import org.apache.commons.text.StringEscapeUtils;

public class HtmlEscapeFilter extends Filter {
	@Override
	public Object doFilter(Object originData) {
		return StringEscapeUtils.escapeHtml4((String) originData);
	}
}
