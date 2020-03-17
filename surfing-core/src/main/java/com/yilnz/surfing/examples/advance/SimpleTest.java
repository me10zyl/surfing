package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.SurfSpider;

public class SimpleTest {
	public static void main(String[] args) {
		System.out.println(SurfSpider.get("http://www.baidu.com").getHtml());
	}
}
