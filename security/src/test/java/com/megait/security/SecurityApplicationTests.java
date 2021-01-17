package com.megait.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
class SecurityApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void contextLoads() {

	}

	@Test
	void securityTest(){
		try {
			mockMvc.perform(get("/hello"))
					.andDo(print())
					.andExpect(status().isUnauthorized()	);
//					.andExpect(view().name("hello"));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
