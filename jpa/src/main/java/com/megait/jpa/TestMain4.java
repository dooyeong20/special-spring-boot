package com.megait.jpa;

import com.megait.jpa.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class TestMain4 {
    public static void main(String[] args) {
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("myunit");

        EntityManager em = factory.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();

            Member member  = new Member();
//            member.setName("Chang");
//            member.setRoleType(RoleType.USER);
//            em.persist(member);

            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
            factory.close();
        }
    }
}
