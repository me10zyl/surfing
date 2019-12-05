package com.yilnz.surfing.ippool;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * curl http://localhost:8080
 */
@SpringBootApplication(scanBasePackages = "com.yilnz.surfing.ippool")
//@MapperScan(basePackages = "com.yilnz.surfing.ippool.dao")
@ImportResource(locations = "spring/spring.xml")
public class SurfingIPPoolMain {
	public static void main(String[] args) {
		SpringApplication.run(SurfingIPPoolMain.class, args);
	}
}
