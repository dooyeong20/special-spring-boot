package com.megait.jpa;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;;

@Entity
@Getter @Setter @ToString
public class Member {

    @Id
    private long id;

    private String name;

}
