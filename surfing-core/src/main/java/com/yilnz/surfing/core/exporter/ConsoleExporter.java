package com.yilnz.surfing.core.exporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleExporter extends Exporter {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleExporter.class);

	@Override
	public void exportList(List<?> list, String... projectFields) {
		for (Object o : list) {
			Field[] declaredFields = o.getClass().getDeclaredFields();
			String string = Arrays.asList(declaredFields).stream().filter(f->{
				if(projectFields == null){
					return true;
				}
				boolean contains = false;
				for (String projectField : projectFields) {
					if(projectField.equals(f.getName())){
						contains = true;
					}
				}
				return contains;
			}).map(f -> {
				Object v = null;
				try {
					f.setAccessible(true);
					v = f.get(o);
				} catch (IllegalAccessException e) {
					logger.error("[surfing]#consoleExporter err", e);
				}
				if(v == null){
					return null;
				}
				return v.toString();
			}).collect(Collectors.joining(" "));
			System.out.println(string);
		}
	}
}
