package com.dy.baeminclone.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private int star;

    private LocalDateTime regdate;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    public void setStore(Store store) {
        this.store = store;
        if(!store.getReviewList().contains(this)){
            store.getReviewList().add(this);
        }
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", star=" + star +
                ", regdate=" + regdate +
                ", store=" + store.getName() +
                '}';
    }
}