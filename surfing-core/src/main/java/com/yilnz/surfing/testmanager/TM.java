package com.yilnz.surfing.testmanager;

import com.yilnz.surfing.core.basic.PlainText;
import com.yilnz.surfing.core.selectors.Selectable;
import com.yilnz.surfing.core.selectors.Selector;
import com.yilnz.surfing.core.selectors.Selectors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class TM {

	private static final String txt = "/var/log/TM/html.txt";

	public static void toTmpFile(String str){
		File f = new File(txt);
		try {
			if(System.currentTimeMillis() - f.lastModified() > 60 * 1000) {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(str.getBytes());
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String readTmpFile(){
		File f = new File(txt);
		try {
			return new String(Files.readAllBytes(f.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void test(Selector selector){
		PlainText pt = new PlainText(readTmpFile());
		System.out.println(pt.select(selector).get());
	}

	public static void testCss(String cssQuery){
		test(Selectors.$(cssQuery));
	}

	public static void main(String[] args) {
		test(Selectors.$(".j_th_tit", "href"));
	}
}
