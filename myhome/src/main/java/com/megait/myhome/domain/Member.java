package com.megait.myhome.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})

@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @Embedded
    private Address address;

    private LocalDateTime joinedAt;

    private boolean emailVerified;

    private String emailCheckToken;

    @Enumerated(EnumType.STRING)
    private MemberType type;


    @OneToMany(fetch = FetchType.LAZY)
    private List<Item> likes = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<Order> orders = new ArrayList<>();


    public void generateEmailCheckToken() {
        emailCheckToken = UUID.randomUUID().toString();
    }

    public boolean hasValidToken(String token) {
        return emailCheckToken != null && emailCheckToken.equals(token);
    }

    public void completeSignup() {
        setEmailVerified(true);
        setJoinedAt(LocalDateTime.now());
    }
}