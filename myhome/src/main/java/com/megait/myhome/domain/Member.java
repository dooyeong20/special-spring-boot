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


    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY)
    private List<Item> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public void generateEmailCheckToken() {
        emailCheckToken = UUID.randomUUID().toString();
    }


    public boolean isValidToken(String token) {
        return token.equals(emailCheckToken);
    }

    public void completeSignup() {
        setEmailVerified(true); // 인증되었음!
        setJoinedAt(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", address=" + address +
                ", joinedAt=" + joinedAt +
                ", emailVerified=" + emailVerified +
                ", emailCheckToken='" + emailCheckToken + '\'' +
                ", type=" + type +
                ", likes=" + likes +
                ", orders=" + orders +
                '}';
    }
}
