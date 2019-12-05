package com.yilnz.surfing.examples.advance;

import com.yilnz.surfing.core.SurfSprider;

public class SimpleTest {
	public static void main(String[] args) {
		System.out.println(SurfSprider.get("http://www.baidu.com").getHtml());
	}
}
