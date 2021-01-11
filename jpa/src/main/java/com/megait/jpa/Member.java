//package com.megait.jpa;
//
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import java.time.LocalDateTime;
//
//@Entity
//@Getter @Setter @ToString
//@Table(name="member")
//public class Member {
//
//    @Id
//    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @Column(name = "name")
//    private String name;
//
//    @Enumerated(EnumType.STRING)
//    private RoleType roleType;
//
////    @Temporal(TemporalType.TIMESTAMP)
////    private Date regdate;
//
//
//    private LocalDateTime regdate;
//
//}
