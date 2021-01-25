package com.dy.baeminclone.repository;

import com.dy.baeminclone.domain.User;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public User save(User user) throws Throwable {
        try{
            em.persist(user);
        } catch (PersistenceException e){
            if(e.getCause() instanceof ConstraintViolationException){
                throw new Throwable("Constraint Violated");
            }
        }

        return user;
    }

    public boolean existsByUser(User user) {
        try {
            return em.createQuery("select u from User u where u.email =:email and u.password =:password", User.class)
                    .setParameter("email", user.getEmail())
                    .setParameter("password", user.getPassword()).getSingleResult() != null;
        } catch (NoResultException e){
            return false;
        }

    }
}
