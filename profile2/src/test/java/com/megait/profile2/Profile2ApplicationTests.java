package com.megait.profile2;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

// Maven 버전

@SpringBootTest
class Profile2ApplicationTests {

	@Autowired
	private MyConfig myConfig;

	Logger logger = LoggerFactory.getLogger(Profile2Application.class);

	@Test
	void contextLoads() {
//		String pet1 = myConfig.cat();
		String pet2 = myConfig.dog();

//		logger.info(pet1);
		logger.info(pet2);

//		Assertions.assertThat(pet1).isNotNull();
		Assertions.assertThat(pet2).isNotNull();
	}

	@Test
	void toyAndSnack(
			@Value("${favorite.snack}") String snack,
			@Value("${favorite.toy}") String toy){

		logger.info("snack : " + snack);
		logger.info("toy : " + toy);
	}

}
