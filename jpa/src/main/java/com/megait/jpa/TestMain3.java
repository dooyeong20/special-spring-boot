//package com.megait.jpa;
//
//import net.bytebuddy.implementation.bytecode.Throw;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.EntityTransaction;
//import javax.persistence.Persistence;
//
////@SpringBootApplication
//public class TestMain3 {
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
//            Long theId = 10L;
//            Member member = em.find(Member.class, theId);
//
//            em.remove(member);
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
