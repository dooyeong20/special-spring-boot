package com.megait.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.HashMap;

public class BeanApplication {

	public static void main(String[] args) {
//		SpringApplication.run(BeanApplication.class, args);
//		==== 위와 아래 두 줄은 같음 ====
//		SpringApplication app = new SpringApplication(BeanApplication.class);
//		app.run(args);

		SpringApplication app = new SpringApplication(BeanApplication.class);
//		app.setWebApplicationType(WebApplicationType.NONE);	// 릴반 자바 프로그램처럼 돌아감 == 웹서비스 실행 X

		HashMap<String, Object> map = new HashMap<>();
		map.put("server.port", 50000);
		app.setDefaultProperties(map);
		app.run(args);

	}

}
