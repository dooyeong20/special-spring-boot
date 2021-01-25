package com.dy.baeminclone.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    public Store(String name) {
        this.name = name;
        this.regdate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime regdate;

    @Column(name = "min_price")
    private int minPrice;

    @Column(name = "delivery_tip")
    private int deliveryTip;

    @Column(name = "delivery_time")
    private int deliveryTime;

    private String location;

    @Builder.Default
    @OneToMany(mappedBy = "store",
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Menu> menuList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store",
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Review> reviewList = new ArrayList<>();

    @Embedded
    private StoreOwner storeOwner;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "store_detail_id")
    private StoreDetail storeDetail;

}
