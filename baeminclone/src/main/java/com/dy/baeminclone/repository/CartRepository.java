package com.dy.baeminclone.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CartRepository {
    @PersistenceContext
    private EntityManager em;

}
