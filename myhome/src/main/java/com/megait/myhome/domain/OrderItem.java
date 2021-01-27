package com.megait.myhome.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter @Getter
@Table(name = "order_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Column(name = "order_price")
    private int orderPrice;

    private int count;
}
