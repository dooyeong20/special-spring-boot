package com.megait.myhome.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "persistent_logins")
@Getter @Setter
public class PersistentLogins {

    @Id
    @Column(length = 64)
    private String series;

    @Column(nullable = false,length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String token;

    @Column(name="last_used", nullable = false, length = 64)
    private LocalDateTime lastUsed;
}
