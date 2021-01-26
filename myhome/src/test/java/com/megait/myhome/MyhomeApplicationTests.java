package com.megait.myhome;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@SpringBootTest
class MyhomeApplicationTests {
	@PersistenceContext
	private EntityManager em;

	@Test
	void contextLoads() {
	}


	@Test
	@Rollback(value = false)
	@Transactional
	void test1(){

	}

}
