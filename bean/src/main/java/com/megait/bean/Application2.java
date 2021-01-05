package com.megait.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Application2 {

	public static void main(String[] args) {
		SpringApplication app =
				new SpringApplication(Application2.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		System.out.println("---- start of main ----");
		app.run(args);

		System.out.println("---- end of main ----");
	}
}