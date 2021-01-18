package com.megait.myhome;

import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.util.ConsoleMailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class MyhomeApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@MockBean  // then() 등에 넣을 객체는 Mocking 객체여야 함
	private ConsoleMailSender consoleMailSender;

	@DisplayName("회원 가입 화면 보이는지 테스트")
	@Test
	void signupForm() throws Exception {
		mockMvc.perform(get("/signup"))
				.andExpect(status().isOk())
				.andExpect(view().name("view/user/signup"))
				.andExpect(model().attributeExists("signupForm"));

	}

	@DisplayName("회원 가입 - 입력값 오류")
	@Test
	void signupSubmit_with_wrong_input() throws Exception {

		mockMvc.perform(post("/signup")
				.param("email", "aa")  // 잘못된 이메일 형식임
				.param("password", "admin1234")
				.param("street", "")
				.param("city", "")
				.param("zipcode", "")
				// 약관 동의도 체크 안 함
				.with(csrf())) // csrf 토큰 설정도 해야 함. (thymeleaf <form>은 csrf 토큰 파라미터가 있음) //  TODO 공부하기 : csrf란?
				.andExpect(status().isOk())
				.andExpect(view().name("view/user/signup"));

	}

	@DisplayName("회원 가입 - 입력값 정상")
	@Test
	@Rollback(value = false)
	void signupSubmit_with_correct_input() throws Exception{
		mockMvc.perform(post("/signup")
				.param("email", "admin@test.com")  // 올바른 이메일 형식
				.param("password", "admin1234")

				.param("street", "")
				.param("city", "")
				.param("zipcode", "")
				.param("agreeTermsOfService", "true")// 약관 동의도 체크 함
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/"));

		// 실제 디비에 들어갔는지도 확인
		assertTrue(memberRepository.existsByEmail("admin@test.com"));

		// 메일 송신 (실제 송신은 아님) send()가 호출 되었는지 확인
		then(consoleMailSender).should().send(any(SimpleMailMessage.class));

	}
}
