package com.dy.baeminclone.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "CART_MENU")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartMenu {

    public CartMenu(Cart cart, Menu menu, int count) {
        this.cart = cart;
        cart.getCartMenuList().add(this);
        this.menu = menu;
        this.count = count;
        this.regdate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private int count;

    private LocalDateTime regdate;

    public void setCart(Cart cart) {
        this.cart = cart;
        cart.getCartMenuList().add(this);
    }

    public void setMenu(Menu menu){
        this.menu = menu;
        menu.getCartMenu().add(this);
    }

    @Override
    public String toString() {
        return "CartMenu{" +
                "id=" + id +
                ", who's cart=" + cart.getUser().getEmail() +
                ", menu=" + menu.getName() +
                ", count=" + count +
                ", regdate=" + regdate +
                '}';
    }
}
