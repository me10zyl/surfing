package com.yilnz.surfing.core.util;

public class XPathUtil {
	public static String equalsText(String element, String text){
		return "//" + element + "[text()='" + text + "']";
	}
}
