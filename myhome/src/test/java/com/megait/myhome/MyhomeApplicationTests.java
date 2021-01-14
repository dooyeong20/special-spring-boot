package com.megait.myhome;

import com.megait.myhome.domain.Album;
import com.megait.myhome.domain.Item;
import com.megait.myhome.domain.Member;
import com.megait.myhome.domain.User;
import com.megait.myhome.repository.UserRepository;
import com.megait.myhome.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.plaf.metal.MetalMenuBarUI;
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

	@Test
	@Transactional
	@Rollback(value = false)
	void ERDtest(){
		Album item = new Album();
		item.setName("정규 1집");
		item.setPrice(30000);
		item.setStockQuantity(500);

		Member member = new Member();
		member.getLikeItems().add(item);

		em.persist(item);
	}

}
