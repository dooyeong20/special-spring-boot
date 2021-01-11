//package com.megait.jpa;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import javax.persistence.EntityManager;
//import javax.transaction.Transactional;
//
//@Component
//@Transactional
//public class MyRunner implements ApplicationRunner {
//
//    private EntityManager em;
//
//    @Autowired
//    public MyRunner(EntityManager em) {
//        this.em = em;
//    }
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        Member member = new Member();
//        member.setName("hello");
//        em.persist(member);
//    }
//}
