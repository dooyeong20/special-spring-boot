package com.megait.myhome;

import com.megait.myhome.domain.User;
import com.megait.myhome.repository.UserRepository;
import com.megait.myhome.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@SpringBootTest
class MyhomeApplicationTests {

	@PersistenceContext
	EntityManager em;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Test
	@Transactional
	@Rollback(value = false)
	void contextLoads() {
		User user = new User();
		user.setUsername("admin");
		user.setPassword("admin1234@");

		Long id = userRepository.insert(user);
		Long id2 = userService.signUp("admin", "admin1234@");

		Assertions.assertThat(em.find(User.class, id).getUsername()).isEqualTo(em.find(User.class, id2).getUsername());
	}

}
