//package com.megait.jpa;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.EntityTransaction;
//import javax.persistence.Persistence;
//
////@SpringBootApplication
//public class TestMain2 {
//
//    public static void main(String[] args) {
//
//        EntityManagerFactory factory =
//                Persistence.createEntityManagerFactory("myunit");
//        EntityManager em = factory.createEntityManager();
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        try{
//            Long theId = 11L;
//
//            Member member = em.find(Member.class, theId);
//
//            // DB를 컬렉션처럼 다루고 싶기 때문에
//            member.setName("피카츄");
//
//            System.out.println("--------------------------");
//            tx.commit();
//            System.out.println("--------------------------");
//        } catch (Exception e){
//            tx.rollback();
//        } finally {
//            em.close();
//            factory.close();
//        }
//
//    }
//
//}
