//package com.megait.jpa;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.EntityTransaction;
//import javax.persistence.Persistence;
//import java.util.List;
//
////@SpringBootApplication
//public class JpaApplication {
//
//	public static void main(String[] args) {
//		EntityManagerFactory factory =
//				Persistence.createEntityManagerFactory("myunit");
//
//		EntityManager em = factory.createEntityManager();
//
//		EntityTransaction tx = em.getTransaction();
//
//		tx.begin();
//
//		Member member = new Member();
//		member.setName("고길동");
//		em.persist(member);
//
//		tx.commit();
//
//		em.close();
//		factory.close();
//
//	}
//
//}
