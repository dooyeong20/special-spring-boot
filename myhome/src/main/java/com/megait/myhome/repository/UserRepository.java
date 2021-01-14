package com.megait.myhome.repository;

import com.megait.myhome.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    public Long insert(User user) {
        em.persist(user);
        return user.getId();
    }
}