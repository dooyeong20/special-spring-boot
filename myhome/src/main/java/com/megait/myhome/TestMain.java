package com.megait.myhome;

import com.megait.myhome.entity.Item;
import com.megait.myhome.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class TestMain {
    public static void main(String[] args) {
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("myunit");

        EntityManager em = factory.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            Member[] members = new Member[3];
            Item[] items = new Item[4];
            Member member = new Member();
            member.setEmail("dfadf");
            member.setAddress("dfasdf");
            em.persist(member);
//            for(Member member : members){
//                member = new Member();
//                member.setEmail("user@testmember.com");
//                member.setAddress("testAddress");
//                member.setAddressDetail("address detail");
//                member.setLastAccessTime("12:00");
//                member.setZipCode("99342");
//
//                em.persist(member);
//            }
//
//            for(Item item : items){
//                item = new Item();
//                item.setName("Book name");
//                item.setPublisher("publisher");
//                item.setPrice(50000);
//
//                em.persist(item);
//            }


            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            factory.close();
        }
    }
}
