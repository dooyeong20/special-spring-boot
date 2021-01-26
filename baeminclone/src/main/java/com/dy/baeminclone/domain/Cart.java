package com.dy.baeminclone.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @ToString(exclude = {"user"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    public Cart(User user) {
        this.user = user;
        user.setCart(this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = {CascadeType.REMOVE})
    private List<CartMenu> cartMenuList = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
        user.setCart(this);
    }
}
