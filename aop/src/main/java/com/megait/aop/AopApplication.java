package com.megait.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class AopApplication {

	public static void main(String[] args) {
//		SpringApplication.run(AopApplication.class, args);
		SpringApplication app = new SpringApplication(AopApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

}

@Component
class MyRunner implements ApplicationRunner {

	@Autowired
	MyService service;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.createSomething();
		service.doSomething();
		service.deleteSomething();

	}
}
