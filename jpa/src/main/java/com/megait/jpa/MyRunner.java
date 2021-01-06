package com.megait.jpa;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

@Component
public class MyRunner implements ApplicationRunner {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void run(ApplicationArguments args) throws Exception {
    }
}
