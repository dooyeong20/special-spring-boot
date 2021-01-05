package com.megait.bean;

import com.megait.bean.config.MyConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:/application.yml")
@ContextConfiguration(classes = MyConfiguration.class)
class BeanApplicationTests {

    @Value("${my.name.first}")
    private String name;

    @Value("${message}")
    private String message;

    @Test
	void contextLoads() {
        Assertions.assertThat(name).isEqualTo("pikachu");
        System.out.println(name);
        System.out.println(message);
	}

}