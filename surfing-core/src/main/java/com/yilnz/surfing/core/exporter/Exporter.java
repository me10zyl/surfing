package com.yilnz.surfing.core.exporter;

import java.util.List;

public abstract class Exporter {
	public void exportList(List<?> list){
		exportList(list);
	}
	public abstract void exportList(List<?> list, String... projectFields);
}
