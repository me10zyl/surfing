package com.yilnz.surfing.core.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
	public static String getFileNameByUrl(String url){
		return getFileNameByUrl(url, "[^/]+?(?=/$|$|\\?)");
	}

	public static String getFileNameByUrl(String url, String regex){
		final Matcher matcher = Pattern.compile(regex).matcher(url);
		matcher.find();
		return matcher.group(0);
	}

	public static String[] seperateFileList(String filesUrls){
		return filesUrls.split("\n");
	}
}
