package com.megait.myhome.repository;

import com.megait.myhome.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {


    @Query("select i from Item i where i.DType = :type")
    List<Item> findByDtype(@Param("type") String type);
}
