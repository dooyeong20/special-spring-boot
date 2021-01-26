package com.dy.baeminclone.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name="USER",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"email"}
                )
        }
)
public class User {

    public User(String username, String password, RoleType roleType, String birth) {
        this.username = username;
        this.password = password;
        this.roleType = roleType;
        this.regdate = LocalDateTime.now();
        this.birth = birth;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cart cart;

    private LocalDateTime regdate;

    private String birth;

    private String email;

    private String tel;

    private String nickname;

    //  private boolean agreement;

    //  private int level;
}