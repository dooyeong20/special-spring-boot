package com.dy.baeminclone.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STORE_DETAIL")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDetail {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String openTime;

    private String closeTime;

    private String closedDayInfo;

    private String deliveryRange;

    @Embedded
    private Address address;

    private long recentOrderCount;

    private long totalOrderCount;

    private long liked;


}
