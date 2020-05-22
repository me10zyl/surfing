package com.yilnz.surfing.core.selectors;

import com.yilnz.surfing.core.basic.HtmlNode;
import com.yilnz.surfing.core.basic.Json;
import com.yilnz.surfing.core.filter.Filter;

import java.util.List;

public interface Selectable {

    String get();

    Selectable select(Selector selector);

    List<Selectable> nodes();

    Integer getInt();


	Json selectJson(String jsonPath);

	HtmlNode selectCss(String cssQuery);

	HtmlNode selectXPath(String xpath);

	Selectable filter(Filter... filters);
}
