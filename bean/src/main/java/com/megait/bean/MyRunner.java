package com.megait.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MyRunner implements CommandLineRunner {
    @Autowired
    Environment environment;

    @Override
    public void run(String... args) throws Exception {
        String name;
        String message;
        int age;

        name = environment.getProperty("my.name.first");
        message = environment.getProperty("message");
        age =  Integer.parseInt(environment.getProperty("my.random.age"));

        System.out.println("name(runner) : " + name);
        System.out.println("message(runner) : " + message);
        System.out.println("age(runner) : " + age);
    }

}