//package com.megait.myhome;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityTransaction;
//
//public class TestMain {
//    @Autowired
//    private static EntityManager em;
//
//    public static void main(String[] args) {
//
//        EntityTransaction tx = em.getTransaction();
//
//        try{
//            tx.begin();
//
//
//            tx.commit();
//        } catch (Exception e){
//            e.printStackTrace();
//            tx.rollback();
//        } finally {
//            em.close();
//        }
//    }
//}
