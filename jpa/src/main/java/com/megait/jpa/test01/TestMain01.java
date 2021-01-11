package com.megait.jpa.test01;//package com.megait.jpa;

import com.megait.jpa.domain.Member;
import com.megait.jpa.domain.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

//@SpringBootApplication
public class TestMain01 {

    public static void main(String[] args) {

        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("myunit");
        EntityManager em = factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();



        try{
            tx.begin();

            Team t1 = new Team();
            t1.setName("회계부");

            Team t2 = new Team();
            t2.setName("사업부");

            em.persist(t1);
            em.persist(t2);

            Member m1 = new Member();
            m1.setName("hong");
            m1.setTeam(t1);

            Member m2 = new Member();
            m2.setName("kim");
            m2.setTeam(t2);

            Member m3 = new Member();
            m3.setName("lee");
            m3.setTeam(t1);

            Member m4 = new Member();
            m4.setName("jeong");
            m4.setTeam(t2);

            em.persist(m1);
            em.persist(m2);
            em.persist(m3);
            em.persist(m4);

            em.flush();
            em.clear();

//            // @MappedBy 확인
            Team team = em.find(Team.class, 1L);
            for(Member m : team.getMembers()){
                System.out.println(m.getName());
            }
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
