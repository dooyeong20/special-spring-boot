package com.dy.baeminclone.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Address {

    private String zipCode;

    private String address;

    private String addressDetail;
}
