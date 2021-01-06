package com.megait.profile2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MyConfig {

    @Bean
    @Profile("catlover")
    public String cat(){
        return "cat";
    }

    @Bean
    @Profile("doglover")
    public String dog() {
        return "dog";
    }
}